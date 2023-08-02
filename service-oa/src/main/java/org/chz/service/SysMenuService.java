package org.chz.service;

import org.chz.model.system.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chz.vo.system.AssignMenuVo;
import org.chz.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author chz
 * @since 2023-07-28
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> getMenusById(Long roleId);

    void doMenuAssign(AssignMenuVo assignMenuVo);

    List<RouterVo> getMenusByUserId(Long userId);

    List<String> getButtonByUserId(Long userId);
}
