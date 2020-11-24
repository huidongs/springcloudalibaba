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
public class LoginRespDTO {
    /**
     * token
     * */
    private JwtTokenRespDTO token;
    /**
     * 用户信息
     * */
    private UserRespDTO user;
}
