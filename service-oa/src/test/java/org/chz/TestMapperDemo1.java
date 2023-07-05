package org.chz;

import org.chz.mapper.SysRoleMapper;
import org.chz.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMapperDemo1 {

    @Autowired
    private SysRoleMapper mapper;   //SysRoleMapper是接口，没有实例对象，最终用的是它的实现类对象。是动态创建的实现类对象，找不到对象

    @Test
    public void getAll(){
        List<SysRole> sysRoles = mapper.selectList(null);
        System.out.println(sysRoles);
    }

    @Test
    public void add(){
        SysRole role = new SysRole();
        role.setRoleName("角色管理员");
        role.setRoleCode("role");
        role.setDescription("角色管理员");
        System.out.println(role);
        mapper.insert(role);
        System.out.println(role.getId());
    }

    @Test
    public void update(){
        SysRole role = mapper.selectById(11);
        role.setRoleName("角色管理员2");
        mapper.updateById(role);

        List<SysRole> sysRoles = mapper.selectList(null);
        System.out.println(sysRoles);
    }

    @Test
    public void deleteLogic(){
        int i = mapper.deleteById(11);
    }

    @Test
    public void deleteMany(){
        mapper.deleteBatchIds(Arrays.asList(9,10));
    }
}
