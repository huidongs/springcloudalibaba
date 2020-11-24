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
public class JwtTokenRespDTO {
    /**
     * token
     * */
    private String token;
    /**
     * 过期时间
     * */
    private Long expirationTime;
}
