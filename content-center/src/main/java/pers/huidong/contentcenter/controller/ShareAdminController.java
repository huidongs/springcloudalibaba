package pers.huidong.contentcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.dto.content.AuditDTO;
import pers.huidong.contentcenter.feignclient.UserCenterFeignClient;
import pers.huidong.contentcenter.service.content.ShareService;

/**
 * @Desc:
 */
@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {

    @Autowired
    private ShareService shareService;
    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id,@RequestBody AuditDTO auditDTO){
        //TODO 认证，授权
        return this.shareService.auditById(id, auditDTO);

    }
}
