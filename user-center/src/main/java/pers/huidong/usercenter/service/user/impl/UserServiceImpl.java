package pers.huidong.usercenter.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.huidong.commons.CommonResult;
import pers.huidong.usercenter.dao.bonus.BonusEventLogMapper;
import pers.huidong.usercenter.dao.user.UserMapper;
import pers.huidong.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import pers.huidong.usercenter.domain.entity.bonus.BonusEventLog;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.UserService;

import java.util.Date;

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
    public CommonResult<User> findById(Integer id){

        return new CommonResult<User>(200,"通过id获取用户信息",this.userMapper.selectByPrimaryKey(id));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(UserAddBonusMsgDTO message) {
        //当收到消息的时候，执行的业务
        //1.为用户加积分
        log.info("开始添加积分");
        Integer userId = message.getUserId();
        Integer bonus = message.getBouns();
        log.info("userId:"+userId+",bonus:"+bonus);
        User user = this.userMapper.selectByPrimaryKey(userId);
        user.setBonus(user.getBonus()+bonus);
        this.userMapper.updateByPrimaryKeySelective(user);
        //2记录日志到bonus_event_log
        this.bonusEventLogMapper.insert(BonusEventLog.builder()
                .createTime(new Date())
                .event("CONTRIBUTE")
                .userId(userId)
                .value(bonus)
                .description("投稿加积分")
                .build()
        );
        log.info("积分添加完毕");
    }

}
