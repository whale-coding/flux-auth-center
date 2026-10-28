package com.flux.common.core.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageData
 * @Author njq
 * @Date 2026/10/28 14:35
 * @Description 分页包装对象，放在Result#data中
 */
@Data
public class PageData<T> implements Serializable {
    private List<T> list;

    private long total;
}
