package org.chz.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.chz.model.system.SysUser;
import org.chz.vo.system.AssignRoleVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author chz
 * @since 2023-07-12
 */
public interface SysUserService extends IService<SysUser> {

    SysUser getUserByUserName(String username);

    Map<String, Object> findRoleDataById(long userId);

    void doAssign(AssignRoleVo assignRoleVo);

    void updateStatus(long id, Integer status);
}
