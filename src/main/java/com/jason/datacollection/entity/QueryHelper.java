package com.jason.datacollection.entity;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 查询助手
 * <pre>
 *     包含分页对象和查询条件对象
 * </pre>
 *
 * @author lyf
 */
@Data
@NoArgsConstructor
public class QueryHelper<T> {

    @Valid
    private T query;
    private Integer page;
    private Integer rows;
}
