package pers.huidong.contentcenter.service.content;

import com.github.pagehelper.PageInfo;
import pers.huidong.contentcenter.domain.dto.content.ShareDTO;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:
 * @Desc:
 */

public interface ShareService {
    ShareDTO findById(Integer id);
    Share auditById(Integer id, AuditDTO auditDTO);

    void auditByIdInDB(Integer id,AuditDTO auditDTO);
    void auditByIdWithRocketMqLog(Integer id,AuditDTO auditDTO,String transactionId);

    PageInfo<Share> q(String title, Integer pageNo, Integer pageSize);

    Share exchangeById(Integer id, HttpServletRequest request);
}
