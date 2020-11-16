package pers.huidong.contentcenter.domain.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.huidong.contentcenter.domain.enums.AuditStatusEnum;

/**
 * @Desc:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDTO {
    /**
     * 审核状态
    * */
    private AuditStatusEnum auditStatusEnum;
    /**
     * 原因
     * */
    private String reason;
}
