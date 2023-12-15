package org.chz.service.impl;

import org.chz.custom.CustomUser;
import org.chz.custom.UserDetailsService;
import org.chz.model.system.SysUser;
import org.chz.service.SysMenuService;
import org.chz.service.SysUserService;
import org.chz.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户
        SysUser user = userService.getUserByUserName(username); //需要在SysUserService中具体实现功能
        if(null == user) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(user.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }

        //根据用户id查询用户操作权限数据
        List<String> buttonList = menuService.getButtonByUserId(user.getId());
        //把权限数据封装成需要的格式
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for (String button : buttonList) {
            authList.add(new SimpleGrantedAuthority(button.trim()));
        }
        return new CustomUser(user, authList);
    }
}
