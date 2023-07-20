package org.chz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.chz.model.system.SysRole;
import org.chz.model.system.SysUser;
import org.chz.result.Result;
import org.chz.service.SysUserService;
import org.chz.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author chz
 * @since 2023-07-12
 */
// localhost:8800/doc.html
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService service;

    //用户条件查询
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable long page,
                      @PathVariable long limit,
                      SysUserQueryVo sysUserQueryVo){

        //创建page对象
        Page<SysUser> pageParam = new Page<>(page,limit);

        //封装条件，判断条件值不为空
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(username)){
            wrapper.like(SysUser::getUsername,username);    //模糊查询
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin); //ge:大于等于
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);   //le：小于等于
        }

        Page<SysUser> pageModel = service.page(pageParam, wrapper);
        return  Result.ok(pageModel);
    }


    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = service.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "新增角色")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysUser user) {
        boolean is_success = service.save(user);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        boolean is_success = service.updateById(user);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = service.removeById(id);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean is_success = service.removeByIds(idList);
        if (is_success)
            return Result.ok();
        else
            return Result.fail();

    }

}

