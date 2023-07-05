package org.chz.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chz.mapper.SysRoleMapper;
import org.chz.model.system.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
