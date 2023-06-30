package com.champ.nocash.security;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserEntityService userEntityService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityService.findUserByMobile(username);
        System.out.println(userEntity);
        if(userEntity != null) {
            if(!userEntity.getIsActive() || userEntity.getIsLocked()) {
                throw new UsernameNotFoundException("account is not active");
            }
            User authUser = new User(
                    userEntity.getMobileNumber(),
                    userEntity.getPin(),
                    Arrays.asList(new SimpleGrantedAuthority("USER"))
            );
            return authUser;
        } else {
            throw new UsernameNotFoundException("Invalid mobile number or pin");
        }
    }
}
