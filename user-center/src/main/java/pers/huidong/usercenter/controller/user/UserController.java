package pers.huidong.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.huidong.usercenter.auth.CheckLogin;
import pers.huidong.usercenter.domain.dto.user.JwtTokenRespDTO;
import pers.huidong.usercenter.domain.dto.user.LoginRespDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.dto.user.UserRespDTO;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.UserService;
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
    private UserService userService;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private JwtOperator jwtOperator;

    @CheckLogin
    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        return this.userService.findById(id);
    }

    /**
     * 微信登录
     *
     * @param loginDTO 请求内容，UserLoginDTO{ code: xxx, userInfo: xxx }
     * @return 登录结果
     */
    @PostMapping("/login")
    public LoginRespDTO wxLogin(@RequestBody UserLoginDTO loginDTO) throws WxErrorException {
        log.info("获取前端传来的信息：{}",loginDTO);
        //微信小程序服务端校验是否已经登录,的结果；本来需要加个返回结果是否为空的判断，空则抛异常，但这里引入了weixin-java-miniapp依赖，内部已做判断
        WxMaJscode2SessionResult result = this.wxMaService.getUserService().getSessionInfo(loginDTO.getCode());
        //这里暂时不需要sessionKey
//        String sessionKey = result.getSessionKey();
        String openId = result.getOpenid();
        //看用户是否注册，如果没有注册就（插入），如果已经注册，就直接颁发token
        User user = this.userService.login(loginDTO, openId);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("wxNickname", user.getWxNickname());
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
        LoginRespDTO loginRespDTO = LoginRespDTO.builder()
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
        log.info("返回给前端的结果{}",loginDTO);
        return loginRespDTO;
    }
}
