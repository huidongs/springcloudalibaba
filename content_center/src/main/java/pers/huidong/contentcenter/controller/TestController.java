package pers.huidong.contentcenter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.huidong.commons.CommonResult;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import pers.huidong.contentcenter.domain.entity.content.Share;
import pers.huidong.contentcenter.domain.entity.content.User;
import pers.huidong.contentcenter.service.content.ShareService;

/**
 * @Desc:
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private ShareMapper shareMapper;

    @GetMapping("/shares/{id}")
    public CommonResult<User> findById(@PathVariable Integer id){
        System.out.println("===========================");
        //获取分享详细
        Share share = shareMapper.selectByPrimaryKey(id);
        log.info("============="+share.toString());
        //发布人id
        Integer userId = share.getUserId();
        //怎么调用用户微服务的/users/{userId}?
        log.info("============="+userId);
        return shareService.getUserInfo(userId);
    }

}
