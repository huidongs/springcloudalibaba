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
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("流控/降级返回的用户");
        return userDTO;
    }

}
