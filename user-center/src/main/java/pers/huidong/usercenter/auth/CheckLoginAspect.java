package pers.huidong.usercenter.auth;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.huidong.usercenter.uitl.JwtOperator;

import javax.servlet.http.HttpServletRequest;

/**
 * @Desc:  AOP实现权限验证
 */
@Aspect
@Component
public class CheckLoginAspect {

    public static final String LOGIN_TOKEN_KEY = "X-StrayAnimal-Token";

    @Around("@annotation(pers.huidong.usercenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {

            //1.从header里面获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();

            String token = request.getHeader(LOGIN_TOKEN_KEY);

            System.out.println("X-Token-StrayAnimal:"+token);
            //2.检验token是否合法&过期

            Boolean isValid = new JwtOperator().validateToken(token);
            System.out.println("isValid:"+isValid);
            if (!isValid){
                throw new SecurityException("Token 不合法");
            }

            //3.如果校验成功，将用户信息设置到request的attribute里面
//            Claims claims = jwtOperator.getClaimsFromToken(token);
//            request.setAttribute("id",claims.get("id"));
//            request.setAttribute("wxNickname",claims.get("wxNickname"));
//            request.setAttribute("role",claims.get("role"));
            return point.proceed();

    }
}
