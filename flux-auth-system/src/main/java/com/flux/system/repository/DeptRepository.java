package com.flux.system.repository;

import com.flux.system.mapper.DeptMapper;
import com.flux.system.model.entity.Dept;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName DeptRepository
 * @Author njq
 * @Date 2026/10/29
 * @Description 部门数据访问
 */
@Repository
public class DeptRepository {

    private final DeptMapper deptMapper;

    public DeptRepository(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    /**
     * 部门及子部门 ID 集合，部门为空时返回空集合
     */
    public Set<Long> listSelfAndChildIds(Long deptId) {
        if (deptId == null) {
            return Set.of();
        }
        return new HashSet<>(deptMapper.selectSelfAndChildIds(deptId));
    }

    /**
     * 部门名称，不存在返回 null
     */
    public String getDeptName(Long deptId) {
        if (deptId == null) {
            return null;
        }
        Dept dept = deptMapper.selectById(deptId);
        return dept == null ? null : dept.getName();
    }
}
