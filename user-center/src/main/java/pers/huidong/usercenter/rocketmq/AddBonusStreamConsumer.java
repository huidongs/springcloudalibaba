package pers.huidong.usercenter.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;
import pers.huidong.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.usercenter.service.user.UserService;


/**
 * @Desc:
 */
@Service
@Slf4j
public class AddBonusStreamConsumer {

    @Autowired
    private UserService userService;

    @StreamListener(Sink.INPUT)
    public void receive(UserAddBonusMsgDTO message) {

        userService.receive(message);
    }
}
