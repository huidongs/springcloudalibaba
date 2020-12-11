package pers.huidong.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    /**
     * 用户名
     * */
    private String username;
    /**
     * 密码
     * */
    private String password;
    /**
     * 电话号码
     * */
    private String mobile;
    /**
     * 短信验证码
     * */
    private String code;
    /**
     * 微信code
     * */
    private String wxCode;
    /**
     * 头像地址
     * */
    private String avatarUrl;
    /**
     * 微信昵称
     * */
    private String nickname;
}
