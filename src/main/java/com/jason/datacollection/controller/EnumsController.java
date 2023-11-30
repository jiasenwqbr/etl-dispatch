package com.jason.datacollection.controller;

import com.jason.datacollection.core.enums.LogLevelEnum;
import com.jason.datacollection.core.enums.RepTypeEnum;
import com.jason.datacollection.core.enums.RunStatusEnum;
import com.jason.datacollection.core.enums.RunTypeEnum;
import com.jason.datacollection.core.povo.EnumInfoDTO;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KDatabaseType;
import com.jason.datacollection.service.KDatabaseTypeService;
import com.jason.datacollection.util.EnumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.database.DatabaseMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一枚举api
 *
 * @author admin
 */
@Api(tags = "统一枚举")
@RequestMapping("/dataCollection/enum")
@RestController
public class EnumsController {

    @Autowired
    KDatabaseTypeService databaseTypeService;

    /**
     * 运行状态枚举类
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "运行状态枚举类")
    @GetMapping("/runStatus")
    Result<List<EnumInfoDTO>> runStatus() {
        return Result.ok(EnumUtil.getEnumInfo(RunStatusEnum.values()));
    }

    /**
     * 脚本运行类型枚举类
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "脚本运行类型枚举类")
    @GetMapping("/runType")
    Result<List<EnumInfoDTO>> runType() {
        return Result.ok(EnumUtil.getEnumInfo(RunTypeEnum.values()));
    }

    /**
     * 数据库访问类型枚举
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "数据库访问类型枚举")
    @GetMapping("/databaseAccessType")
    Result<List<EnumInfoDTO>> databaseAccessType() {
        // 数据库访问类型编码
        String[] dbAccessTypeCode = DatabaseMeta.dbAccessTypeCode;
        // 数据库访问类型编码说明
        String[] dbAccessTypeDesc = DatabaseMeta.dbAccessTypeDesc;
        List<EnumInfoDTO> list = new ArrayList<>();
        for (int i = 0; i < dbAccessTypeCode.length; i++) {
            EnumInfoDTO dto = new EnumInfoDTO();
            dto.setCode(dbAccessTypeCode[i]);
            dto.setValue(dbAccessTypeDesc[i]);
            list.add(dto);
        }
        return Result.ok(list);
    }

    /**
     * 日志级别枚举
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "日志级别枚举")
    @GetMapping("/logLevel")
    Result<List<EnumInfoDTO>> logLevel() {
        return Result.ok(EnumUtil.getEnumInfo(LogLevelEnum.values()));
    }

    /**
     * 资源库类型枚举
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "资源库类型枚举")
    @GetMapping("/repositoryType")
    Result<List<EnumInfoDTO>> repositoryType() {
        return Result.ok(EnumUtil.getEnumInfo(RepTypeEnum.values()));
    }

    /**
     * 数据库类型列表
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "数据库类型列表")
    @GetMapping("/databaseType")
    Result<List<EnumInfoDTO>> databaseType() {
        List<KDatabaseType> dbTypeList = databaseTypeService.list();
        List<EnumInfoDTO> collect = dbTypeList.stream().map(databaseTypeRes -> {
            EnumInfoDTO dto = new EnumInfoDTO();
            dto.setCode(databaseTypeRes.getCode());
            dto.setValue(databaseTypeRes.getDescription());
            return dto;
        }).collect(Collectors.toList());
        return Result.ok(collect);
    }
}
