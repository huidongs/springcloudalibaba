package pers.huidong.contentcenter.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pers.huidong.contentcenter.auth.CheckLogin;
import pers.huidong.contentcenter.domain.dto.content.ShareDTO;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.service.content.ShareService;

import javax.servlet.http.HttpServletRequest;

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
        return this.shareService.findById(id);
    }

    @GetMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        if (pageSize > 100) {
            pageSize = 100;
        }
        return this.shareService.q(title,pageNo,pageSize);
    }

    @CheckLogin
    @GetMapping("/exchange/{id}")
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request){
        return this.shareService.exchangeById(id,request);

    }

}
