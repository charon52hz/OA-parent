package org.chz.service.impl;

import org.chz.custom.CustomUser;
import org.chz.custom.UserDetailsService;
import org.chz.model.system.SysUser;
import org.chz.service.SysUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private SysUserService userService;
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
        return new CustomUser(user, Collections.emptyList());
    }
}
