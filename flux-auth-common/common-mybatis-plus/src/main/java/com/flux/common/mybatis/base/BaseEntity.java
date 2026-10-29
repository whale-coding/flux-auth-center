package com.flux.common.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName BaseEntity
 * @Author njq
 * @Date 2026/10/27 23:57
 * @Description 基础实体类。
 * 审计字段由 MyMetaObjectHandler 自动填充，逻辑删除由 deleted 字段上的 @TableLogic 控制：
 * 0 表示未删除，删除时写入 UNIX_TIMESTAMP()，配合唯一索引 (xxx, deleted) 允许删除后重建同名数据。
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识：0 未删除，大于 0 为删除时间戳
     */
    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    private Long deleted;
}
