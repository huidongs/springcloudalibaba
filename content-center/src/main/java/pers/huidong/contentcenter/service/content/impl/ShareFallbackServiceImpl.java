package pers.huidong.contentcenter.service.content.impl;

import pers.huidong.commons.CommonResult;
import pers.huidong.contentcenter.domain.entity.content.User;
import pers.huidong.contentcenter.service.content.ShareService;

/**
 * @Desc:
 */
public class ShareFallbackServiceImpl implements ShareService {

    @Override
    public CommonResult<User> getUserInfo(Integer id) {
        return new CommonResult<>(44444,"服务降级--ShareFallbackServiceImpl");
    }
}
