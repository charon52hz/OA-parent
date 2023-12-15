package org.chz.filter;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chz.custom.CustomUser;
import org.chz.jwt.JwtHelper;
import org.chz.result.Result;
import org.chz.result.ResultCodeNum;
import org.chz.utils.ResponseUtil;
import org.chz.vo.system.LoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisTemplate redisTemplate;

    //构造方法
    public TokenLoginFilter(AuthenticationManager authenticationManager,RedisTemplate redisTemplate) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
        this.redisTemplate = redisTemplate;
    }

    //登录认证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());

            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //登录成功
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUser customUser = (CustomUser) authResult.getPrincipal();
        String token = JwtHelper.createToken(customUser.getSysUser().getId(), customUser.getSysUser().getUsername());

        //把权限数据保存在redis中
        redisTemplate.opsForValue().set(customUser.getUsername(), JSON.toJSONString(customUser.getAuthorities()));
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ResponseUtil.out(response, Result.ok(map));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if(failed.getCause() instanceof RuntimeException) {
            ResponseUtil.out(response, Result.build(null, 204, failed.getMessage()));
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeNum.LOGIN_ERROR));
        }
    }
}
