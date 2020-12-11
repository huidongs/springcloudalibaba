package pers.huidong.usercenter.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.huidong.usercenter.dao.bonus.BonusEventLogMapper;
import pers.huidong.usercenter.dao.user.UserMapper;
import pers.huidong.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.entity.bonus.BonusEventLog;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.UserService;
import pers.huidong.usercenter.uitl.ResponseUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static pers.huidong.usercenter.uitl.WxResponseCode.AUTH_INVALID_ACCOUNT;

/**
 * @Desc:
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BonusEventLogMapper bonusEventLogMapper;

    @Override
    public User findById(Integer id) {
        return this.userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBonus(UserAddBonusMsgDTO megDTO) {
        //当收到消息的时候，执行的业务
        //1.为用户加积分
        log.info("开始添加积分");
        Integer userId = megDTO.getUserId();
        Integer bonus = megDTO.getBouns();
        log.info("userId:" + userId + ",bonus:" + bonus);
        User user = this.userMapper.selectByPrimaryKey(userId);
        user.setBonus(user.getBonus() + bonus);
        this.userMapper.updateByPrimaryKeySelective(user);
        //2记录日志到bonus_event_log
        this.bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .createTime(new Date())
                        .event(megDTO.getEvent())
                        .userId(userId)
                        .value(bonus)
                        .description(megDTO.getDescription())
                        .build()
        );
        log.info("积分添加完毕");
    }

    /**
     * @param loginDTO,openId
     * @return user
     * @deprecated wx用户登录, 判断是否已注册
     */
    @Override
    public User login(UserLoginDTO loginDTO, String openId) {
        //根据openId获取用户信息
        User user = this.userMapper.selectOne(
                User.builder().wxOpenid(openId).build()
        );
        //没有注册则初始化用户信息
        if (user == null) {
            User userToSave = User.builder()
                    .wxOpenid(openId)
                    .bonus(300)
                    .nickname(loginDTO.getNickname())
                    .avatarUrl(loginDTO.getAvatarUrl())
                    .roles("user")
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now()).build();
            this.userMapper.insertSelective(userToSave);
            return userToSave;
        }
        //注册了则直接返回user
        return user;
    }

    @Override
    public List<User> queryByUsername(String username) {
        return this.userMapper.select(User.builder().username(username).build());
    }

    @Override
    public List<User> queryByMobile(String mobile) {
        return this.userMapper.select(User.builder().mobile(mobile).build());
    }

    @Override
    public List<User> queryByOpenid(String openId) {
        return this.userMapper.select(User.builder().wxOpenid(openId).build());
    }

    @Override
    public void add(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    @Override
    public int updateById(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }


}
