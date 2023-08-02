package org.chz.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chz.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author chz
 * @since 2023-07-28
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getMenusByUserId(@Param("userId") Long userId);
}
