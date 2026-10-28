package com.flux.common.core.base;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName BaseEntity
 * @Author njq
 * @Date 2026/10/27 23:57
 * @Description 基础实体类
 */
@Data
public class BaseEntity implements Serializable {
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
