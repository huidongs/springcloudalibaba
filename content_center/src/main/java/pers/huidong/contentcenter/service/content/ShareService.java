package pers.huidong.contentcenter.service.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.huidong.contentcenter.dao.content.ShareMapper;
import pers.huidong.contentcenter.domain.entity.content.Share;

/**
 * @Desc:
 */
@Service
public class ShareService {
    @Autowired
    private ShareMapper shareMapper;

    public Share findById(Integer id){
        //获取分享详情
        Share share =  this.shareMapper.selectByPrimaryKey(id);
        //分享人id
        Integer userId = share.getUserId();
        //怎么调用用户微服务的/users/{userId}
        return null;
    }

}
