package pers.huidong.contentcenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desc:
 */
@RestController
public class TestController {

    @Autowired
    private Source source;

    @GetMapping("/test-stream")
    public String test() {
        this.source.output()
                .send(
                        MessageBuilder
                        .withPayload("消息体")
                        .build()
                );
        return "success";
    }

}
