package com.flux.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flux.system.model.entity.SysDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName SysDeptMapper
 * @Author njq
 * @Date 2026/10/29
 * @Description 部门表 Mapper
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 当前部门及所有子部门 ID（DEPT_AND_CHILD 数据范围用）。
     * MySQL 8 的递归 CTE，不依赖 tree_path 的格式，脏数据也不会串部门
     */
    @Select("""
            with recursive dept_tree as (
                select id from sys_dept where id = #{deptId} and deleted = 0
                union all
                select d.id
                from sys_dept d
                join dept_tree t on d.parent_id = t.id
                where d.deleted = 0
            )
            select id from dept_tree
            """)
    List<Long> selectSelfAndChildIds(@Param("deptId") Long deptId);
}
