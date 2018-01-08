package com.example.demo.config;

import com.example.demo.config.jwt.JwtUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Create by ky.bai on 2018-01-06 16:59
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) throw new UsernameNotFoundException("登录失败，帐号密码错误.");

        //权限,暂时都配置权限为USER
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        String password = new BCryptPasswordEncoder().encode("123456");
        return new JwtUser(1L, username, password, true, null, authorities);
    }

}
