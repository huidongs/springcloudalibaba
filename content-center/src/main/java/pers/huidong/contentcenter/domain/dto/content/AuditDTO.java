package pers.huidong.contentcenter.domain.dto.content;

import lombok.Data;
import pers.huidong.contentcenter.domain.enums.AuditStatusEnum;

/**
 * @Desc:
 */
@Data
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
