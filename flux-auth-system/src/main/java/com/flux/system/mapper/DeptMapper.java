package com.flux.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flux.system.model.entity.Dept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName DeptMapper
 * @Author njq
 * @Date 2026/10/29
 * @Description 部门表 Mapper
 */
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 当前部门及所有子部门 ID（DEPT_AND_CHILD 数据范围用）
     */
    List<Long> selectSelfAndChildIds(@Param("deptId") Long deptId);
}
