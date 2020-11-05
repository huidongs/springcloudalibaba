package pers.huidong.usercenter.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.huidong.commons.CommonResult;
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
    public CommonResult<User> findById(Integer id){

        return new CommonResult<User>(200,"通过id获取用户信息",this.userMapper.selectByPrimaryKey(id));
    }
}
