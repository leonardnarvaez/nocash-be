package com.champ.nocash.security;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {

    @Autowired
    private UserEntityRepository userEntityRepository;

    public UserEntity getUserEntity() {
        return userEntityRepository.findFirstByMobileNumber(getSessionUser());
    }
    public static String getUserId() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUser currentUser = (CustomUser) authentication.getPrincipal();
            return currentUser.getUserId();
        }
        return null;
    }

    public static String getIPAddress() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            String ipAddress = details.getRemoteAddress();
            return ipAddress;
        }
        return null;
    }
    public static String getSessionUser() {
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUser = authentication.getName();
            return currentUser;
        }
        return null;
    }


}
