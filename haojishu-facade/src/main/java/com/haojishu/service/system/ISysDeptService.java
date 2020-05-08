package com.haojishu.service.system;

import com.haojishu.annotation.DataScope;
import com.haojishu.core.domain.Ztree;
import com.haojishu.domain.SysDept;
import com.haojishu.domain.SysRole;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author sulwan@126.com
 */
@Component
public interface ISysDeptService {
  /**
   * 查询部门管理数据
   *
   * @param dept 部门信息
   * @return 部门信息集合
   */
  @DataScope(deptAlias = "d")
  public List<SysDept> selectDeptList(SysDept dept);

  /**
   * 查询部门管理树
   *
   * @param dept 部门信息
   * @return 所有部门信息
   */
  @DataScope(deptAlias = "d")
  public List<Ztree> selectDeptTree(SysDept dept);

  /**
   * 根据角色ID查询菜单
   *
   * @param role 角色对象
   * @return 菜单列表
   */
  public List<Ztree> roleDeptTreeData(SysRole role);

  /**
   * 查询部门人数
   *
   * @param parentId 父部门ID
   * @return 结果
   */
  public int selectDeptCount(Long parentId);

  /**
   * 查询部门是否存在用户
   *
   * @param deptId 部门ID
   * @return 结果 true 存在 false 不存在
   */
  public boolean checkDeptExistUser(Long deptId);

  /**
   * 删除部门管理信息
   *
   * @param deptId 部门ID
   * @return 结果
   */
  public int deleteDeptById(Long deptId);

  /**
   * 新增保存部门信息
   *
   * @param dept 部门信息
   * @return 结果
   */
  public int insertDept(SysDept dept);

  /**
   * 修改保存部门信息
   *
   * @param dept 部门信息
   * @return 结果
   */
  public int updateDept(SysDept dept);

  /**
   * 根据部门ID查询信息
   *
   * @param deptId 部门ID
   * @return 部门信息
   */
  public SysDept selectDeptById(Long deptId);

  /**
   * 校验部门名称是否唯一
   *
   * @param dept 部门信息
   * @return 结果
   */
  public String checkDeptNameUnique(SysDept dept);
}
