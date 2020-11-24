package pers.huidong.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.huidong.commons.CommonResult;
import pers.huidong.usercenter.domain.dto.user.JwtTokenRespDTO;
import pers.huidong.usercenter.domain.dto.user.LoginRespDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.dto.user.UserRespDTO;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.impl.UserServiceImpl;
import pers.huidong.usercenter.uitl.JwtOperator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc:
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private JwtOperator jwtOperator;

    @GetMapping("/{id}")
    public CommonResult<User> findById(@PathVariable Integer id) {
        return this.userService.findById(id);
    }

    @GetMapping("/login")
    public LoginRespDTO wxLogin(@RequestBody UserLoginDTO loginDTO) throws WxErrorException {
        //微信小程序服务端校验是否已经登录的结果
        WxMaJscode2SessionResult result = this.wxMaService.getUserService()
                .getSessionInfo(loginDTO.getCode());
        //微信的openId，用户在这边的唯一标识
        String openId = result.getOpenid();
        //看用户是否注册，如果没有注册就（插入），如果已经注册，就直接颁发token
        User user = this.userService.login(loginDTO, openId);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("wxNicename", user.getWxNickname());
        userInfo.put("role", user.getRoles());
        //获取信息
        String token = jwtOperator.generateToken(userInfo);
        Date expirationTime = jwtOperator.getExpirationDateFromToken(token);

        log.info("用户{}登录成功，生成的token = {}，有效期到：{}",
                loginDTO.getWxNickname(),
                token,
                expirationTime
        );
        //构建响应
        return LoginRespDTO.builder()
                .token(JwtTokenRespDTO.builder()
                        .token(token)
                        .expirationTime(expirationTime.getTime()).build())
                .user(UserRespDTO.builder()
                        .id(user.getId())
                        .avatarUrl(user.getAvatarUrl())
                        .bonus(user.getBonus())
                        .wxNickname(user.getWxNickname())
                        .build())
                .build();
    }
}
