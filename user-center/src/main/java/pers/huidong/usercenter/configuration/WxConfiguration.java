package pers.huidong.usercenter.configuration;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc:
 */
@Configuration
public class WxConfiguration {
    @Bean
    public WxMaConfig wxMaConfig(){
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid("wxd5a7d915a2aed5a9");
        config.setSecret("75a3eb1148669f18843746de5bcf9cb6");
        return config;
    }
    @Bean
    public WxMaService wxMaService(WxMaConfig wxMaConfig){
        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(wxMaConfig);
        return service;
    }
/**
 *  其实用openid已经可以标识用户，可以将openid直接返回给客户端，以后的访问服务器操作带上openid参数即可，
 *  但是openid是一个非常隐私的用户数据，微信小程序推荐服务器自己根据openid生成sessionId，利用sessionId来标识用户，
 *  因此我们可以随便使用一个加密算法去实现，sessionId的生成
 *  （因为使用加密算法的明文和密文是一一对应的，openid不同自然生成的sessionId也不同，因此可以标识用户），
 *  这里我采用的是md5（在hashlib库中）。
 *
 *  sessionId = hashlib,md5(openid.encode(encoding='utf-8')).hexdigest()
 *
 *  然后将sessionId返回给微信客户端，采用json格式发送返回数据
 *  不直接使用flask中的自动包装response，而是自己生成response对象。
 *  若要返回json格式的数据，要在返回的headers中设置content-type为application/json。
 *  再利用make_response生成返回对象，注意参数无法直接通过字符串写出json格式数据，
 *  需要使用jsonify方法（这个方法来自flask中的jsonify库）
 *
 *  headers = {
 * 	'content-type': 'application/json',
 *        }
 *  response = make_response(jsonify({'sessionId: sessionId'}),200)
 *
 *  获取到sessionId后，我们每次访问服务器时都要将访问用户进行标识，
 *  所以每次访问服务器都要带上sessionId。因此我们需要将sessionId注册到整个微信小程序的全局变量中。
 *  实现的方法是整个小程序的app.js中去设置全局变量
 *
 *  globalData: {
 * 	userInfo: null,
 * 	sessionId: null,
 *        }
 *
 *  globalData的作用是声明全局变量，其中的sessionId就是每次访问服务器需要带上用以标识用户。
 * （我们的标识用户采用的是加密后的openid因此这个与服务器会话的密钥不会改变，因此不会过期）
 *  在实现上如果每次使用小程序都去访问服务器获取sessionId显然是很浪费资源的，因此我们可以利用微信小程序中的缓存功能，
 *  将请求到的sessionId缓存到本地。在app.js中OnLaunch()生命周期中写入获取sessionId并设置全局变量的代码。
 * */
}
