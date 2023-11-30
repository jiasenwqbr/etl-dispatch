package com.jason.datacollection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.DiScript;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiScriptMapper extends BaseMapper<DiScript> {
    void deleteByCategoryIdInAndRepId(@Param("categorys") List<String> categorys,@Param("repId") String repId);
    List<DiScript> findByCategoryIdAndRepId(@Param("categoryId")String categoryId,@Param("repId") String repId);
    List<DiScript> findByCategoryPidAndRepId(@Param("categoryId") String categoryId, @Param("repId") String repId);
    List<DiScript> findByCategoryIdInAndRepId(@Param("categorys")List<String> s,@Param("repId") String repId);
    List<DiScript> findByRepIdAndType(String repId,String type);
    List<DiScript> findByCategoryIdInAndRepIdAndType(List<String> s, String repId, String type);
    List<DiScript> findByRepId(String repId);
    void deleteByRepId(@Param("repId") String repId);
    void insertAll(List<DiScript> diScripts);
}
