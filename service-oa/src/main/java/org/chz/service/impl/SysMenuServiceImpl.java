package org.chz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.chz.common.config.exception.MyException;
import org.chz.model.system.SysMenu;
import org.chz.mapper.SysMenuMapper;
import org.chz.model.system.SysRoleMenu;
import org.chz.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chz.service.SysRoleMenuService;
import org.chz.util.MenuHelper;
import org.chz.vo.system.AssignMenuVo;
import org.chz.vo.system.MetaVo;
import org.chz.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author chz
 * @since 2023-07-28
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService roleMenuService;

    @Override
    public void removeMenuById(Long id) {
        //删除时判断菜单是否有下一层，如果有则不能直接删除
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new MyException(500,"此菜单不能删除");
        }else
            baseMapper.deleteById(id);
        
    }

    //根据角色id 获取 对应的菜单
    @Override
    public List<SysMenu> getMenusById(Long roleId) {
        //1 获取全部菜单
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> sysMenus = baseMapper.selectList(menuWrapper);

        //2 获取角色id 对应的 菜单id
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<Long> idList = roleMenuService.list(roleMenuWrapper).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        //3 角色对应的菜单id 与 全部菜单 比较，如果存在，则取出
        for (SysMenu menu : sysMenus) {
            if (idList.contains(menu.getId())){
                menu.setSelect(true);
            }else
                menu.setSelect(false);
        }
        List<SysMenu> roleMenus = MenuHelper.buildTree(sysMenus);
        return roleMenus;

    }

    //为角色分配菜单
    @Override
    public void doMenuAssign(AssignMenuVo assignMenuVo) {
        //1 根据角色id，删除该角色的所有菜单
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        roleMenuService.remove(wrapper);

        //2 把新分配的 该角色的菜单id列表 添加到角色-菜单表中
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId))
                continue;
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(assignMenuVo.getRoleId());
            roleMenu.setMenuId(menuId);
            roleMenuService.save(roleMenu);
        }
    }

    //获取用户可以操作的按钮
    @Override
    public List<String> getButtonByUserId(Long userId) {
        List<SysMenu> buttons =null;
        if (userId.longValue() == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            buttons = baseMapper.selectList(wrapper);
        }else {
            buttons = baseMapper.getMenusByUserId(userId);
        }
        //2 取出菜单列表中的按钮层列表中的perms参数
        List<String> perms = buttons.stream().filter(button -> button.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return perms;
    }

    //获取用户可以操作的菜单
    @Override
    public List<RouterVo> getMenusByUserId(Long userId) {
        List<SysMenu> menus = null;
        //1 判断用户是否是admin管理员，userId=1 表示admin，拥有所有菜单权限。
        // 如果不是管理员，查询可以操作的菜单列表（多表查询，用户角色表-角色菜单表-菜单表）
        if (userId.longValue() == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            menus = baseMapper.selectList(wrapper);
        }else {
            menus = baseMapper.getMenusByUserId(userId);
        }
        //2 把查询出来的数据列表 构建成框架要求的路由结构
        List<SysMenu> menusTree = MenuHelper.buildTree(menus);
        List<RouterVo> routerList = this.buildRouter(menusTree);

        return routerList;
    }

    private List<RouterVo> buildRouter(List<SysMenu> menusTree) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menusTree) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(),menu.getIcon()));
            List<SysMenu> children = menu.getChildren();

            if (menu.getType().intValue() == 1){    //菜单层
                //加载下一层的隐藏路由
                List<SysMenu> hiddenMenus = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());

                for (SysMenu hiddenMenu : hiddenMenus) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(),hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else { //如果不是菜单层，递归地构建下一层
                if (!CollectionUtils.isEmpty(children)){
                    if (children.size()>0){
                        router.setAlwaysShow(true);     //有下一层菜单时，当前层菜单总是显示
                    }
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    private String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenus = baseMapper.selectList(null);

        //构建树形结构
        List<SysMenu> result =  MenuHelper.buildTree(sysMenus);

        return result;
    }
}
