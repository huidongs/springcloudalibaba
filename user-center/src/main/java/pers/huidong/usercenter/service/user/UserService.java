package pers.huidong.usercenter.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.huidong.usercenter.dao.user.UserMapper;
import pers.huidong.usercenter.domain.entity.user.User;

/**
 * @Desc:
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findById(Integer id){
        return this.userMapper.selectByPrimaryKey(id);
    }
}
