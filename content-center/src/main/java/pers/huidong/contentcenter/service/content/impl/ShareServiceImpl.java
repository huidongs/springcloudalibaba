package pers.huidong.contentcenter.service.content.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import pers.huidong.contentcenter.dao.content.RocketmqTransactionLogMapper;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import pers.huidong.contentcenter.domain.dto.content.ShareDTO;
import pers.huidong.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.contentcenter.domain.dto.user.UserDTO;
import pers.huidong.contentcenter.domain.entity.content.RocketmqTransactionLog;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;
import pers.huidong.contentcenter.domain.enums.AuditStatusEnum;
import pers.huidong.contentcenter.feignclient.UserCenterFeignClient;
import pers.huidong.contentcenter.service.content.ShareService;

import java.util.Objects;
import java.util.UUID;

import static pers.huidong.contentcenter.domain.global.Contant.LOGIN_TOKEN_KEY;

/**
 * @Desc:
 */
@Service
@Slf4j
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    @Autowired
    private UserCenterFeignClient userCenterFeignClient;
    @Autowired
    private Source source;

    @Override
    public ShareDTO findById(Integer id) {
        //获取分享详细
        Share share = shareMapper.selectByPrimaryKey(id);
        //发布人id
        Integer userId = share.getUserId();
        // 1. 代码不可读
        // 2. 复杂的url难以维护：https://user-center/s?ie={ie}&f={f}&rsv_bp=1&rsv_idx=1&tn=baidu&wd=a&rsv_pq=c86459bd002cfbaa&rsv_t=edb19hb%2BvO%2BTySu8dtmbl%2F9dCK%2FIgdyUX%2BxuFYuE0G08aHH5FkeP3n3BXxw&rqlang=cn&rsv_enter=1&rsv_sug3=1&rsv_sug2=0&inputT=611&rsv_sug4=611
        // 3. 难以相应需求的变化，变化很没有幸福感
        // 4. 编程体验不统一
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        ShareDTO shareDTO = new ShareDTO();
        // 消息的装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    @Override
    public Share auditById(Integer id, AuditDTO auditDTO) {
        //1.查询share是否存在，不存在或者当前的audit_status ! = NOT_YET
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        if (share==null){
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET",share.getAuditStatus())){
            throw new IllegalArgumentException("参数非法！该分享已通过审核！");
        }
        //3.如果是PASS,那么为发布人添加积分：：syn为增加用户体验和接口效率，这里使用异步执行，可选方法有cTemplate,@sync,webClient以及MQ
        if(AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())){
            String transactionId = UUID.randomUUID().toString();
            //发送半消息
            this.source.output().send(
                    MessageBuilder.withPayload(
                            UserAddBonusMsgDTO.builder()
                                    .userId(userId)
                                    .bouns(50)
                                    .build()
                    )
                            //header也有妙用
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", userId)
                            .setHeader("dto", JSON.toJSONString(auditDTO))
                            .build()
            );
        }else {
            this.auditByIdInDB(userId,auditDTO);
        }

        return share;
    }

    /**
     *  处理本地数据库
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id,AuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);
        //4 数据库缓存
    }
    /**
     *  处理本地数据库,并记录了事务日志
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id,AuditDTO auditDTO,String transactionId){
        this.auditByIdInDB(id,auditDTO);
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .id(id)
                        .transactionId(transactionId)
                        .log("审核分享...")
                        .build()
        );
    }

}
