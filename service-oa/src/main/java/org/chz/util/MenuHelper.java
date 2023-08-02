package org.chz.util;

import org.chz.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {

    public static List<SysMenu> buildTree(List<SysMenu> sysMenus) {
        List<SysMenu> treeMenu = new ArrayList<>();

        for (SysMenu menu : sysMenus) {
            if (menu.getParentId() == 0){
                treeMenu.add(getChildren(menu,sysMenus));
            }
        }
        return  treeMenu;
    }

    public static SysMenu getChildren(SysMenu menu,List<SysMenu> list){
        menu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu item: list){
            if (item.getParentId().longValue() == menu.getId().longValue()){
                menu.getChildren().add(getChildren(item,list));
            }
        }
        return menu;
    }
}
