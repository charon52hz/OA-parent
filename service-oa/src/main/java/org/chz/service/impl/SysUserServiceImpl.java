package org.chz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.chz.mapper.SysUserMapper;
import org.chz.model.system.SysRole;
import org.chz.model.system.SysUser;
import org.chz.model.system.SysUserRole;
import org.chz.service.SysRoleService;
import org.chz.service.SysUserRoleService;
import org.chz.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chz.vo.system.AssignRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author chz
 * @since 2023-07-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleService userRoleService;
    
    @Autowired
    private SysRoleService roleService;

    @Override
    public Map<String, Object> findRoleDataById(long userId) {
        //1. 查询所有角色，返回list集合
        List<SysRole> allRoles = roleService.list(null);

        //2. 根据userId查询角色用户表，userId对应的角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> userRoles = userRoleService.list(wrapper);
            //取出角色id
        List<Long> idList = userRoles.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //3. 根据查到的角色id，找到对应的角色信息
        List<SysRole> assignRoles = new ArrayList<>();
        for (SysRole role: allRoles){
            if (idList.contains(role.getId())){
                assignRoles.add(role);
            }
        }
        //4. 放入map中
        Map<String,Object> roleMap = new HashMap<>();
        roleMap.put("allRoles",allRoles);
        roleMap.put("assignRoles",assignRoles);
        return roleMap;
    }

    @Override
    public void updateStatus(long id, Integer status) {
        SysUser user = baseMapper.selectById(id);
        user.setStatus(status);
        baseMapper.updateById(user);
    }

    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        //1. 把用户之前的角色删除-> 在用户角色表中，根据用户id进行删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assignRoleVo.getUserId());
        userRoleService.remove(wrapper);

        //2. 重新分配角色
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            userRoleService.save(sysUserRole);
        }

    }


}
