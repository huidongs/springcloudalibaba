package pers.huidong.contentcenter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import pers.huidong.contentcenter.auth.CheckLogin;
import pers.huidong.contentcenter.domain.dto.content.ShareDTO;
import pers.huidong.contentcenter.service.content.ShareService;

import static pers.huidong.contentcenter.domain.global.Contant.LOGIN_TOKEN_KEY;

/**
 * @Desc:
 */
@Slf4j
@RestController
public class ShareController {

    @Autowired
    private ShareService shareService;

    @GetMapping("/shares/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable("id") Integer id) {
        return shareService.findById(id);
    }

}
