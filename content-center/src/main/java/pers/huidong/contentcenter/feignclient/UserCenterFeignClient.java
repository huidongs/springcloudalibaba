package pers.huidong.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.huidong.commons.CommonResult;
import pers.huidong.contentcenter.domain.dto.user.UserDTO;
import pers.huidong.contentcenter.feignclient.fallback.ShareServiceFallback;

/**
 * @Desc:
 */

@FeignClient(value = "user-center",fallback = ShareServiceFallback.class)
public interface UserCenterFeignClient {
    // 两个坑：1. @GetMapping不支持   2. @PathVariable得设置value

    @GetMapping("/users/{id}")
    CommonResult<UserDTO> getUserInfo(@PathVariable("id") Integer id);
}
