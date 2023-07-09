package com.champ.nocash.security;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findFirstByMobileNumber(username);
        if(userEntity != null) {
            if(!userEntity.getIsActive()) {
                throw new UsernameNotFoundException("account is not active");
            }
            return new CustomUser(
                    userEntity.getMobileNumber(),
                    userEntity.getPin(),
                    Arrays.asList(new SimpleGrantedAuthority("USER")),
                    userEntity.getId()
            );
        } else {
            throw new UsernameNotFoundException("Invalid mobile number or pin");
        }
    }
    public boolean isUserAccountLocked(String username) {
        UserEntity userEntity = userEntityRepository.findFirstByMobileNumber(username);
        return userEntity.getIsLocked();
    }
}
