package pers.huidong.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huidong
 * @Desc:
 */
@SpringBootApplication
@MapperScan("pers.huidong.contentcenter.dao.content")
@EnableFeignClients
@EnableBinding({Source.class})
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);

    }
}
