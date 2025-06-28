package io.github.lunasaw.voglander.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lunasaw.voglander.common.exception.ServiceException;
import io.github.lunasaw.voglander.common.exception.ServiceExceptionEnum;
import io.github.lunasaw.voglander.manager.assembler.RoleAssembler;
import io.github.lunasaw.voglander.manager.domaon.dto.RoleDTO;
import io.github.lunasaw.voglander.manager.service.RoleService;
import io.github.lunasaw.voglander.repository.entity.MenuDO;
import io.github.lunasaw.voglander.repository.entity.RoleDO;
import io.github.lunasaw.voglander.repository.mapper.MenuMapper;
import io.github.lunasaw.voglander.repository.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author luna
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public IPage<RoleDTO> getRoleList(RoleDTO dto) {
        Page<RoleDO> page = new Page<>(
            dto.getPageNum() != null ? dto.getPageNum() : 1,
            dto.getPageSize() != null ? dto.getPageSize() : 10);

        // 构建查询条件
        LambdaQueryWrapper<RoleDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(dto.getRoleName()), RoleDO::getRoleName, dto.getRoleName())
            .eq(dto.getStatus() != null, RoleDO::getStatus, dto.getStatus())
            .orderByDesc(RoleDO::getCreateTime);

        IPage<RoleDO> rolePage = roleMapper.selectPage(page, queryWrapper);

        // 转换结果并填充权限信息
        IPage<RoleDTO> result = new Page<>();
        result.setCurrent(rolePage.getCurrent());
        result.setSize(rolePage.getSize());
        result.setTotal(rolePage.getTotal());
        result.setPages(rolePage.getPages());

        List<RoleDTO> roleDTOList = RoleAssembler.toDTOList(rolePage.getRecords());

        // 为每个角色查询并设置权限信息
        if (roleDTOList != null) {
            roleDTOList.forEach(roleDTO -> {
                List<MenuDO> menuList = menuMapper.selectMenusByRoleId(roleDTO.getId());
                roleDTO.setPermissions(RoleAssembler.menuListToPermissions(menuList));
            });
        }

        result.setRecords(roleDTOList);
        return result;
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        if (id == null) {
            return null;
        }

        RoleDO roleDO = roleMapper.selectById(id);
        if (roleDO == null) {
            return null;
        }

        RoleDTO dto = RoleAssembler.toDTO(roleDO);

        // 查询角色权限
        List<MenuDO> menuList = menuMapper.selectMenusByRoleId(id);
        dto.setPermissions(RoleAssembler.menuListToPermissions(menuList));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(RoleDTO dto) {
        RoleDO roleDO = RoleAssembler.createRoleDO(dto);
        int result = roleMapper.insert(roleDO);

        if (result > 0) {
            // 创建角色权限关联
            if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
                // 查询所有菜单，用于权限标识符转换
                List<MenuDO> allMenus = menuMapper.selectList(null);
                List<Long> menuIds = RoleAssembler.permissionsToMenuIds(dto.getPermissions(), allMenus);
                updateRolePermissions(roleDO.getId(), menuIds);
            }
            log.info("创建角色成功，角色ID：{}，权限数量：{}", roleDO.getId(),
                dto.getPermissions() != null ? dto.getPermissions().size() : 0);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Long id, RoleDTO dto) {
        RoleDO existingRole = roleMapper.selectById(id);
        if (existingRole == null) {
            throw new ServiceException(ServiceExceptionEnum.BUSINESS_EXCEPTION.getCode(), "角色不存在");
        }

        // 更新角色基本信息
        RoleAssembler.updateRoleDO(existingRole, dto);
        int result = roleMapper.updateById(existingRole);

        if (result > 0) {
            // 更新角色权限关联
            if (dto.getPermissions() != null) {
                // 查询所有菜单，用于权限标识符转换
                List<MenuDO> allMenus = menuMapper.selectList(null);
                List<Long> menuIds = RoleAssembler.permissionsToMenuIds(dto.getPermissions(), allMenus);
                updateRolePermissions(id, menuIds);
            } else {
                updateRolePermissions(id, null);
            }
            log.info("更新角色成功，角色ID：{}，权限数量：{}", id,
                dto.getPermissions() != null ? dto.getPermissions().size() : 0);
            return true;
        }
        return false;
    }

    /**
     * 更新角色权限关联
     *
     * @param roleId 角色ID
     * @param permissions 权限列表（菜单ID列表）
     */
    private void updateRolePermissions(Long roleId, List<Long> permissions) {
        // 先删除该角色的所有权限关联
        roleMapper.deleteRoleMenuByRoleId(roleId);

        // 如果有新的权限，批量插入
        if (permissions != null && !permissions.isEmpty()) {
            roleMapper.batchInsertRoleMenu(roleId, permissions);
            log.info("更新角色权限成功，角色ID：{}，权限菜单ID：{}", roleId, permissions);
        } else {
            log.info("清空角色权限，角色ID：{}", roleId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        RoleDO role = roleMapper.selectById(id);
        if (role == null) {
            throw new ServiceException(ServiceExceptionEnum.BUSINESS_EXCEPTION.getCode(), "角色不存在");
        }

        // 检查是否有用户使用该角色
        // 这里可以根据业务需求添加检查逻辑

        // 先删除角色权限关联
        roleMapper.deleteRoleMenuByRoleId(id);

        int result = roleMapper.deleteById(id);
        if (result > 0) {
            log.info("删除角色成功，角色ID：{}", id);
            return true;
        }
        return false;
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long userId) {
        List<RoleDO> roleList = roleMapper.selectRolesByUserId(userId);
        return RoleAssembler.toDTOList(roleList);
    }
}