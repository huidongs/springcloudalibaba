package pers.huidong.contentcenter.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @Desc: 这个类不用加@Configuration注解，否则需要将这个类移到componentScan扫描到的包以外；
 */
public class UserCenterFeignConfiguration {

    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
