package pers.huidong.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pers.huidong.usercenter.auth.CheckLogin;
import pers.huidong.usercenter.domain.dto.user.JwtTokenRespDTO;
import pers.huidong.usercenter.domain.dto.user.LoginRespDTO;
import pers.huidong.usercenter.domain.dto.user.UserLoginDTO;
import pers.huidong.usercenter.domain.dto.user.UserRespDTO;
import pers.huidong.usercenter.domain.entity.user.User;
import pers.huidong.usercenter.service.user.UserService;
import pers.huidong.usercenter.uitl.JwtOperator;
import pers.huidong.usercenter.uitl.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pers.huidong.usercenter.uitl.WxResponseCode.*;

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
    public Object wxLogin(@RequestBody UserLoginDTO loginDTO) throws WxErrorException {
        log.info("获取前端传来的信息：{}", loginDTO);
        //微信小程序服务端校验是否已经登录,的结果；本来需要加个返回结果是否为空的判断，空则抛异常，但这里引入了weixin-java-miniapp依赖，内部已做判断
        WxMaJscode2SessionResult result = this.wxMaService.getUserService().getSessionInfo(loginDTO.getWxCode());
        //这里暂时不需要sessionKey
        String sessionKey = result.getSessionKey();
        String openId = result.getOpenid();
        //看用户是否注册，如果没有注册就（插入），如果已经注册，就直接颁发token
        User user = this.userService.login(loginDTO, openId);

        Map<String, Object> userInfo = new HashMap<>(16);
        userInfo.put("id", user.getId());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("role", user.getRoles());
        //获取信息
        String token = jwtOperator.generateToken(userInfo);
        Date expirationTime = jwtOperator.getExpirationDateFromToken(token);

        log.info("用户{}登录成功，生成的token = {}，有效期到：{}",
                loginDTO.getNickname(),
                token,
                expirationTime
        );
        //构建响应
        LoginRespDTO loginRespDTO = LoginRespDTO.builder()
                .token(JwtTokenRespDTO.builder()
                        .token(token)
                        .expirationTime(expirationTime.getTime()).build())
                .userInfo(UserRespDTO.builder()
                        .id(user.getId())
                        .avatarUrl(user.getAvatarUrl())
                        .bonus(user.getBonus())
                        .nickname(user.getNickname())
                        .build())
                .build();

        Object resultOk = ResponseUtil.ok(loginRespDTO);
        log.info("返回给前端的结果{}", resultOk);
        return resultOk;
    }
    /**
     * 账号登录
     *
     * @param loginDTO 请求内容，UserLoginDTO{ xxx }
     * @return 登录结果
     */
    @PostMapping("/login_by_account")
    public Object login_by_account(@RequestBody UserLoginDTO loginDTO){
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        if (username == null || password == null) {
            return ResponseUtil.badArgument();
        }
        List<User> userList = userService.queryByUsername(username);
        User user = null;
        if (userList.size() > 1) {
            return ResponseUtil.serious();
        } else if (userList.size() == 0) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号不存在");
        } else {
            user = userList.get(0);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号密码不对");
        }

        // 更新登录情况
        user.setLastLoginTime(LocalDateTime.now());
//        user.setLastLoginIp(IpUtil.getIpAddr(request));
        if (userService.updateById(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        Map<String, Object> userInfo = new HashMap<>(16);
        userInfo.put("id", user.getId());
        // token
        String token = jwtOperator.generateToken(userInfo);

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }
    /**
     * 账号注册
     *
     * @param loginDTO 请求内容，UserLoginDTO{ xxx }
     * @return 登录结果
     */
    @PostMapping("/register")
    public Object register(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) throws WxErrorException {

        System.out.println("=====UserLoginDTO======"+loginDTO);
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String mobile = loginDTO.getPassword();
        String code = loginDTO.getPassword();
        // 如果是小程序注册，则必须非空,其他情况，可以为空
        String wxCode = loginDTO.getWxCode();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(mobile)
                || StringUtils.isEmpty(code)) {
            return ResponseUtil.badArgument();
        }
        //看用户是否注册，如果没有注册就（插入）
        List<User> userList = userService.queryByUsername(username);
        if (userList.size() > 0) {
            return ResponseUtil.fail(AUTH_NAME_REGISTERED, "用户名已注册");
        }
        userList = userService.queryByMobile(mobile);
        if (userList.size() > 0) {
            return ResponseUtil.fail(AUTH_MOBILE_REGISTERED, "手机号已注册");
        }
//        if (!RegexUtil.isMobileExact(mobile)) {
//            return ResponseUtil.fail(AUTH_INVALID_MOBILE, "手机号格式不正确");
//        }
        //判断验证码是否正确
//        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile);
//        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
//            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
//        }
        String openId = "";
        // 非空，则是小程序注册
        // 继续验证openid
        if(!StringUtils.isEmpty(wxCode)) {
            WxMaJscode2SessionResult result = this.wxMaService.getUserService().getSessionInfo(wxCode);
            openId = result.getOpenid();
        }
            userList = userService.queryByOpenid(openId);
            if (userList.size() > 1) {
                return ResponseUtil.serious();
            }
            if (userList.size() == 1) {
                User user = userList.get(0);
                String checkUsername = user.getUsername();
                String checkPassword = user.getPassword();
                if (!checkUsername.equals(openId) || !checkPassword.equals(openId)) {
                    return ResponseUtil.fail(AUTH_OPENID_BINDED, "openid已绑定账号");
                }
            }
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .mobile(mobile)
                .wxOpenid(openId)
                .avatarUrl("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64")
                .nickname(username)
                .gender((byte) 0)
                .lastLoginTime(LocalDateTime.now())
                .build();

        userService.add(user);

        // 给新用户发送注册优惠券
//        couponAssignService.assignForRegister(user.getId());

        // userInfo
        Map<String, Object> userInfo = new HashMap<>(16);
        userInfo.put("id", user.getId());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("role", user.getRoles());
        //获取信息
        String token = jwtOperator.generateToken(userInfo);

        Map<Object, Object> result = new HashMap<Object, Object>(16);
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }
}
