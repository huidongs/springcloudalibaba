package pers.huidong.contentcenter.auth;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.huidong.contentcenter.uitl.JwtOperator;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.Objects;

import static pers.huidong.contentcenter.domain.global.Contant.LOGIN_TOKEN_KEY;

/**
 * @Desc: AOP实现权限验证
 */
@Slf4j
@Aspect
@Component
public class AuthAspect {


    @Autowired
    private JwtOperator jwtOperator;

    @Around("@annotation(pers.huidong.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        checkToken();
        return point.proceed();
    }

    @Around("@annotation(pers.huidong.contentcenter.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point) throws Throwable {
        try {
            //这里的try应该包含住全部内容，使即使在检验token非法时也能被自定义异常捕获，而不是响应500；
            //1.验证token是否合法
            checkToken();
            //2.检验用户角色是否匹配
            HttpServletRequest request = getHttpServletRequest();
            Object role = request.getAttribute("role");
            System.out.println("======role=="+role);
            //获取注解中的value值
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);
            String value = annotation.value();
            System.out.println("=========value===="+value);
            if (!Objects.equals(role, value)) {
                throw new SecurityException("用户无权访问！");
            }
        } catch (Throwable throwable) {
            throw new SecurityException("用户无权访问！", throwable);
        }
        return point.proceed();
    }

    private void checkToken() {
        try {
            //突然出现字符不合法有可能的原因是：从HttpServletRequest对象获取token时，系统有个过滤器将请求参数中的特殊字符过滤掉了
            //1.从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader(LOGIN_TOKEN_KEY);
            log.info("接收到的Token = {}", token);
            //   2.检验token是否合法&过期
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                throw new SecurityException("Token 不合法");
            }
            //  3.如果校验成功，将用户信息设置到request的attribute里面
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
        } catch (Throwable throwable) {
            throw new SecurityException("Token 不合法");
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        return request;
    }


}
