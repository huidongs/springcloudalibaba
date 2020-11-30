package pers.huidong.contentcenter.feignclient.interceptor;

import com.mysql.cj.util.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static pers.huidong.contentcenter.domain.global.Contant.LOGIN_TOKEN_KEY;

/**
 * @author
 * @Desc:
 */
public class TokenRelayRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //1.获取到token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader(LOGIN_TOKEN_KEY);
        //2.将token传递
        if (!StringUtils.isNullOrEmpty(token)){
            requestTemplate.header(LOGIN_TOKEN_KEY,token);
        }
    }
}
