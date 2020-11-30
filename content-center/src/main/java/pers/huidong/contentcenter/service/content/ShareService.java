package pers.huidong.contentcenter.service.content;

import org.springframework.web.bind.annotation.RequestHeader;
import pers.huidong.contentcenter.domain.dto.content.ShareDTO;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;

import static pers.huidong.contentcenter.domain.global.Contant.LOGIN_TOKEN_KEY;

/**
 * @Author:
 * @Desc:
 */

public interface ShareService {
    ShareDTO findById(Integer id);
    Share auditById(Integer id, AuditDTO auditDTO);

    void auditByIdInDB(Integer id,AuditDTO auditDTO);
    void auditByIdWithRocketMqLog(Integer id,AuditDTO auditDTO,String transactionId);
}
