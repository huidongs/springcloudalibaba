package pers.huidong.contentcenter.feignclient.fallback;

import org.springframework.stereotype.Service;
import pers.huidong.commons.CommonResult;
import pers.huidong.contentcenter.domain.dto.user.UserDTO;
import pers.huidong.contentcenter.feignclient.UserCenterFeignClient;

/**
 * @Desc:
 */
@Service
public class ShareServiceFallback implements UserCenterFeignClient {
    @Override
    public CommonResult<UserDTO> getUserInfo(Integer id) {
        return new CommonResult<>(44444,"服务降级--ShareFallbackServiceImpl");
    }

}
