package com.flux.common.core.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BasePageQuery
 * @Author njq
 * @Date 2026/10/28 00:01
 * @Description 基础分页请求对象
 */
@Data
public class BasePageQuery implements Serializable {

    /**
     * 页码
     */
    private int pageNum = 1;

    /**
     * 每页记录数
     */
    private int pageSize = 10;
}
