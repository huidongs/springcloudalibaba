package pers.huidong.usercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: HuiDong XU
 * @Description:
 * @Date: 2020/10/25 11:58
 * @Version: 1.0
 */
@MapperScan("pers.huidong.usercenter.dao")
@SpringBootApplication
@EnableBinding({Sink.class})
public class UserCenterApplication {
    public static void main(String[] args){
         SpringApplication.run(UserCenterApplication.class,args);
    }
}
