package pers.huidong.contentcenter.service.content;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.huidong.commons.CommonResult;
import pers.huidong.contentcenter.domain.entity.content.User;
import pers.huidong.contentcenter.service.content.impl.ShareFallbackServiceImpl;

/**
 * @Desc:
 */
@FeignClient(value = "user-center",fallback = ShareFallbackServiceImpl.class)
public interface ShareService {

    @GetMapping("/users/{id}")
    CommonResult<User> getUserInfo(@PathVariable("id") Integer id);

}
