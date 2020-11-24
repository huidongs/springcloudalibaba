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
     * code
     * */
    private String code;
    /**
     * 头像地址
     * */
    private String avatarUrl;
    /**
     * 微信昵称
     * */
    private String wxNickname;
}
