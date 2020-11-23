package pers.huidong.contentcenter.rocketmq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import pers.huidong.contentcenter.dao.content.RocketmqTransactionLogMapper;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;
import pers.huidong.contentcenter.domain.entity.content.RocketmqTransactionLog;
import pers.huidong.contentcenter.service.content.ShareService;

/**
 * @Desc:
 */
@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
@Slf4j
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private ShareService shareService;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;


    /**
     * 执行本地数据库操作
     * */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("我进入了executeLocalTransaction");
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer share_id = Integer.valueOf((String) headers.get("share_id"));
        AuditDTO auditDTO = JSON.parseObject((String) headers.get("dto"), AuditDTO.class);

        try {
            log.info("我进入了executeLocalTransaction中的try");
            this.shareService.auditByIdWithRocketMqLog(share_id, auditDTO, transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.info("我进入了executeLocalTransaction中的catch");
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
    /**
     * 回查
     * */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("我进入了checkLocalTransaction");
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        RocketmqTransactionLog transactionLog = rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (transactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
