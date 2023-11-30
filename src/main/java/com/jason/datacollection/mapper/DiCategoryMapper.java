package com.jason.datacollection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.DiCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface DiCategoryMapper extends BaseMapper<DiCategory> {
    List<DiCategory> selectByRepId(String repid);
    DiCategory findByCategoryIdAndRepId(@Param("categoryId") String categoryId,@Param("repId")String repId);
    List<DiCategory> findByRepIdOrderByCode(String repId);
    List<DiCategory> findByIsDefaultAndRepId(@Param("isDefault") String isDefault,@Param("repId") String repId);
    List<DiCategory> findByCategoryPidAndRepId(@Param("caregoryPid") String caregoryPid,@Param("repId")String repId);
    DiCategory selectById(String id);
    void deleteByCategoryIdInAndRepId(@Param("categorys") List<String> categorys, @Param("repId") String repId);
    void deleteByRepId(@Param("repId") String repId);
    void insertAll(List<DiCategory> diCategories);
}
