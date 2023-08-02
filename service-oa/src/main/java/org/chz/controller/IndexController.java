package org.chz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.errorprone.annotations.Var;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.chz.common.config.exception.MyException;
import org.chz.jwt.JwtHelper;
import org.chz.model.system.SysUser;
import org.chz.result.Result;
import org.chz.service.SysMenuService;
import org.chz.service.SysUserService;
import org.chz.utils.MD5;
import org.chz.vo.system.LoginVo;
import org.chz.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService userService;


    @Autowired
    private SysMenuService menuService;
    /**
     * 后台登录
     * @return
     */

    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
//        HashMap<String , Object> map = new HashMap<>();
//        map.put("token","admin");
        //判断用户是否存在
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUsername,username);
        SysUser user = userService.getOne(userWrapper);
        if (user == null){
            throw new MyException(500,"用户不存在");
        }
        //判断密码
        if (!user.getPassword().equals(MD5.encrypt(loginVo.getPassword()))){
            throw new MyException(500,"密码错误");
        }
        //判断用户是否被禁用
        if (user.getStatus().intValue()==0){
            throw new MyException(500,"用户被禁用");
        }

        //根据id和name生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getUsername());
        HashMap<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);

    }

    /**
     * 获取后台登录用户信息
     * @return
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1. 从请求头获取token字符串
        String header = request.getHeader("token");
        //2. 根据token字符串获取用户id
        Long userId = JwtHelper.getUserId(header);
        //3. 根据用户id获取用户信息
        SysUser user = userService.getById(userId);
        //4. 根据用户id获取用户可以操作的菜单列表
            //动态构建路由
        List<RouterVo> routers = menuService.getMenusByUserId(userId);
        //5. 根据用户id获取用户可以操作的按钮列表
        List<String> buttons = menuService.getButtonByUserId(userId);
        //6. 返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles",user.getRoleList());
        map.put("name",user.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers",routers);
        map.put("buttons",buttons);
        return Result.ok(map);
    }

    /**
     * 后台管理系统退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
