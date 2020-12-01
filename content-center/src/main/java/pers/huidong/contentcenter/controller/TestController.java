package pers.huidong.contentcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
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

    @Value("{your.configuration}")
    private String yourConfiguration;

    @GetMapping("/test-config")
    public String getYourConfiguration(){
        return this.yourConfiguration;
    }


}
