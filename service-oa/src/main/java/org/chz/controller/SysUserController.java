package org.chz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.chz.model.system.SysUser;
import org.chz.result.Result;
import org.chz.service.SysUserService;
import org.chz.utils.MD5;
import org.chz.vo.system.AssignRoleVo;
import org.chz.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author chz
 * @since 2023-07-12
 */
//localhost:8800/doc.html
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
        String keywords = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(keywords)){
            wrapper.like(SysUser::getUsername,keywords).or().like(SysUser::getName,keywords).or().like(SysUser::getPhone,keywords);    //模糊查询
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

    @ApiOperation("根据用户id更改用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable long id,@PathVariable Integer status){
        service.updateStatus(id,status);
        return Result.ok();
    }

    @ApiOperation("为用户获得角色数据")
    @GetMapping("toAssign/{userId}")
    public Result toAssign(@PathVariable long userId){
        Map<String, Object> roleMap =  service.findRoleDataById(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assignRoleVo){
        service.doAssign(assignRoleVo);
        return Result.ok();
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
        //对密码进行MD5加密
        String MD5password = MD5.encrypt(user.getPassword());
        user.setPassword(MD5password);
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

