package pers.huidong.contentcenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huidong
 * @Desc:
 */
@SpringBootApplication
@MapperScan("pers.huidong")
@EnableFeignClients
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);

    }
}
