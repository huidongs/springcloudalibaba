package pers.huidong.contentcenter.dao.content;

import org.apache.ibatis.annotations.Param;
import pers.huidong.contentcenter.domain.entity.content.Share;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ShareMapper extends Mapper<Share> {
    List<Share> selectByParam(@Param("title") String title);
}