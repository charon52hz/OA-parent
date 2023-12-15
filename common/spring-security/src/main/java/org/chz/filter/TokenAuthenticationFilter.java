package org.chz.filter;

import com.alibaba.fastjson.JSON;
import org.chz.jwt.JwtHelper;
import org.chz.result.Result;
import org.chz.result.ResultCodeNum;
import org.chz.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {


    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(httpServletRequest.getRequestURI())) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(httpServletRequest);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            ResponseUtil.out(httpServletResponse, Result.build(null, ResultCodeNum.PERMISSION));
        }

    }


    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:"+token);
        if (!StringUtils.isEmpty(token)) {
            String useruame = JwtHelper.getUsername(token);
            logger.info("useruame:"+useruame);
            if (!StringUtils.isEmpty(useruame)) {

                //通过username从redis中获取权限数据
                String authString = (String) redisTemplate.opsForValue().get(useruame);
                //把redis中字符串类型的数据转换为所需的集合数据类型List<SimpleGrantedAuthority>
                if (!StringUtils.isEmpty(authString)){
                    List<Map> mapList = JSON.parseArray(authString, Map.class);
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    for (Map map : mapList) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority((String)(map.get("authority")));
                        authorities.add(authority);
                    }
                    return new UsernamePasswordAuthenticationToken(useruame, null, authorities);
                }else {
                    return new UsernamePasswordAuthenticationToken(useruame, null, Collections.emptyList());
                }
            }
        }
        return null;
    }
}
