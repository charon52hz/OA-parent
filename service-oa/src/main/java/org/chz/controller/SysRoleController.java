package org.chz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.chz.common.config.exception.MyException;
import org.chz.model.system.SysRole;
import org.chz.result.Result;
import org.chz.service.SysRoleService;
import org.chz.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    // http://localhost:8800/admin/system/sysRole/findAll
    @ApiOperation(value = "获取全部角色列表")
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll(){
        List<SysRole> list = sysRoleService.list();
//        try {
//            int i = 10/0;
//        }catch (Exception e){
//            throw new MyException(500,"处理自定义异常");
//        }

        return Result.ok(list);
    }

    /**
     * 条件分页查询
     * page 当前页
     * limit 每页显示的记录数
     */

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable long page, @PathVariable long limit, SysRole sysRoleQueryVo){
        //调用service方法实现
        //1. 创建page对象，传递分页相关的参数
        Page<SysRole> pageParam = new Page<>(page,limit);

        //2. 封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName,roleName);    //封装条件查询，like:模糊查询
        }

        //3. 传入分页需要的参数和查询条件，调用方法实现分页条件查询
        Page<SysRole> result = sysRoleService.page(pageParam, wrapper);
        return Result.ok(result);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation(value = "新增角色")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysRole role) {
        boolean is_success = sysRoleService.save(role);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public Result updateById(@RequestBody SysRole role) {
        boolean is_success = sysRoleService.updateById(role);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = sysRoleService.removeById(id);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean is_success = sysRoleService.removeByIds(idList);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();

    }

}
