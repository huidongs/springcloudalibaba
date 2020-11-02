package pers.huidong.contentcenter;

import org.springframework.boot.SpringApplication;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huidong
 * @Desc:
 */
@SpringBootApplication
@MapperScan("pers.huidong")
public class ContentCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }
}
