package org.chz.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.chz.model.system.SysMenu;
import org.chz.result.Result;
import org.chz.service.SysMenuService;
import org.chz.vo.system.AssignMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author chz
 * @since 2023-07-28
 */
@ApiOperation("菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService service;

    @ApiOperation("根据角色获取菜单")
    @GetMapping("getMenus/{roleId}")
    public Result getMenusById(@PathVariable Long roleId){
        List<SysMenu> menus = service.getMenusById(roleId);
        return Result.ok(menus);
    }


    @ApiOperation("给角色分配菜单")
    @PostMapping("doMenuAssign")
    public Result doMenuAssign(@RequestBody AssignMenuVo assignMenuVo){
        service.doMenuAssign(assignMenuVo);
        return Result.ok();
    }

    @ApiOperation("获取菜单")
    @GetMapping("findNodes")
    public Result findNodes(){
        List<SysMenu> list =  service.findNodes();
        return Result.ok(list);
    }


    @ApiOperation("新增菜单")
    @PostMapping("/save")
    public Result save(@RequestBody SysMenu menu){
        service.save(menu);
        return Result.ok();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        service.removeMenuById(id);
        return Result.ok();
    }

    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu menu){
        service.updateById(menu);
        return Result.ok();

    }
}

