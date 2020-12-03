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
@AllArgsConstructor
@NoArgsConstructor
public class UserAddBonusDTO {
    /**
     * id
     */
    private Integer userId;
    /**
     * 积分
     */
    private Integer bonus;

    private String event;

    private String description;
}
