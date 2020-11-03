package pers.huidong.usercenter.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.huidong.usercenter.dao.user.UserMapper;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.UserService;

/**
 * @Desc:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Integer id){
        return this.userMapper.selectByPrimaryKey(id);
    }
}
