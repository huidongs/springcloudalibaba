package pers.huidong.usercenter.service.user;

import org.springframework.stereotype.Service;
import pers.huidong.usercenter.domain.entity.user.User;

/**
 * @Desc:
 */

public interface UserService {

    User findById(Integer id);
}
