package pers.huidong.usercenter.service.user;

import org.springframework.stereotype.Service;
import pers.huidong.commons.CommonResult;
import pers.huidong.usercenter.domain.entity.user.User;

/**
 * @Desc:
 */

public interface UserService {

    CommonResult<User> findById(Integer id);
}
