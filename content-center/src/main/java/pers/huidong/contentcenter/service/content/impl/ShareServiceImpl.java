package pers.huidong.contentcenter.service.content.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import pers.huidong.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;
import pers.huidong.contentcenter.service.content.ShareService;

import java.util.Objects;

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

    @Override
    public Share auditById(Integer id, AuditDTO auditDTO) {
        //1.查询share是否存在，不存在或者当前的audit_status ! = NOT_YET
        System.out.println("=============进入auditById=================");
        Share share = this.shareMapper.selectByPrimaryKey(id);
        System.out.println("================share================="+share);
        log.info("查询share是否存在,share:"+share);
        if (share==null){
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET",share.getAuditStatus())){
            throw new IllegalArgumentException("参数非法！该分享已通过审核！");
        }
        //2.审核资源，将状态设置为PASS/REJECT
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        this.shareMapper.updateByPrimaryKey(share);
        log.info("更新share:"+share);
        //3.如果是PASS,那么为发布人添加积分：为增加用户体验和接口效率，这里使用异步执行，可选方法有：syncTemplate,@sync,webClient以及MQ
        if ("PASS".equals(share.getAuditStatus())){
            rocketMQTemplate.convertAndSend("add-bonus", UserAddBonusMsgDTO.builder()
                    .userId(share.getUserId())
                    .bouns(50)
                    .build());
        }
        return share;
    }

    @Override
    public String test() {
        return "success";
    }
}
