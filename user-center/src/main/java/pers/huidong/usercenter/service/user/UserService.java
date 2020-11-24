package pers.huidong.usercenter.service.user;


import pers.huidong.commons.CommonResult;
import pers.huidong.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.entity.user.User;

/**
 * @Desc:
 */

public interface UserService {

    CommonResult<User> findById(Integer id);

    void receive(UserAddBonusMsgDTO message);

    User login(UserLoginDTO loginDTO, String openId);
}
