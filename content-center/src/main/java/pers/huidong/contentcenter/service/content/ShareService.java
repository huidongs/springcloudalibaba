package pers.huidong.contentcenter.service.content;

import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;

/**
 * @Author:
 * @Desc:
 */

public interface ShareService {

    Share auditById(Integer id, AuditDTO auditDTO);

    String test();
}
