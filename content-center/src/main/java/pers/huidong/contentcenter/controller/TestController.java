package pers.huidong.contentcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Desc:
 */
@RestController
public class TestController {

    MessageChannel messageChannel = new DirectChannel();
    @Autowired
    private Source source;

    @GetMapping("/test-stream")
    public String test() {

        this.source.output().send(
                        MessageBuilder.withPayload("消息体").build()
        );
        return "success";
    }


}
