package pers.huidong.usercenter.service.user;



import pers.huidong.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.entity.user.User;

import java.util.List;

/**
 * @Desc:
 */

public interface UserService {
    User findById(Integer id);

    void addBonus(UserAddBonusMsgDTO message);

    User login(UserLoginDTO loginDTO, String openId);

    List<User> queryByUsername(String username);

    List<User> queryByMobile(String mobile);

    List<User> queryByOpenid(String openId);

    void add(User user);

    int updateById(User user);
}
