package pers.huidong.contentcenter.service.content.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.huidong.contentcenter.dao.content.RocketmqTransactionLogMapper;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import pers.huidong.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.contentcenter.domain.entity.content.RocketmqTransactionLog;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;
import pers.huidong.contentcenter.domain.enums.AuditStatusEnum;
import pers.huidong.contentcenter.service.content.ShareService;

import java.util.Objects;
import java.util.UUID;

/**
 * @Desc:
 */
@Service
@Slf4j
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Override
    public Share auditById(Integer id, AuditDTO auditDTO) {
        //1.查询share是否存在，不存在或者当前的audit_status ! = NOT_YET
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share==null){
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET",share.getAuditStatus())){
            throw new IllegalArgumentException("参数非法！该分享已通过审核！");
        }
        //3.如果是PASS,那么为发布人添加积分：：syn为增加用户体验和接口效率，这里使用异步执行，可选方法有cTemplate,@sync,webClient以及MQ
        if(AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())){
            String transactionId = UUID.randomUUID().toString();
            //System.out.println("====serviceImpl中====transactionId:"+transactionId);
            //发送半消息
            this.rocketMQTemplate.sendMessageInTransaction(
                    "add-bonus",
                    MessageBuilder.withPayload(
                            UserAddBonusMsgDTO.builder()
                            .userId(share.getUserId())
                            .bouns(50)
                            .build()
                    )
                            //header也有妙用
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(),
                            //arg有大用处
                            auditDTO
            );
        }else {
            this.auditByIdInDB(id,auditDTO);
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
