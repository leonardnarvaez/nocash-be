package com.champ.nocash.controller;

import com.champ.nocash.bean.RegisterBean;
import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.enums.AuthenticationType;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private AuthenticationHistoryService authenticationHistoryService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private SecurityUtil securityUtil;
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        AuthenticationResponse authenticationResponse = null;
        try {
            authenticationResponse = userEntityService.login(authenticationRequest);
        } catch (BadCredentialsException badCredentialsException) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Not Found")
                    .message(badCredentialsException.getMessage())
                    .status(404)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Internal Error")
                    .message("You've done goof")
                    .status(500)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() throws Exception {
        UserEntity loggedInUser = securityUtil.getUserEntity();
        if(loggedInUser != null) {
            // there is a logged in user
            AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                    .userId(loggedInUser.getId())
                    .isAuthenticationResultSuccess(true)
                    .authenticationType(AuthenticationType.LOGOUT)
                    .build();
            authenticationHistoryService.save(authenticationHistoryEntity);
            return ResponseEntity.ok(new HashMap<String, String>(){{
                put("message", "logout success");
            }});
        } else {
            return ResponseEntity.badRequest().body(new HashMap<String, String>(){{
                put("error", "not logged-in");
            }});
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterBean registerBean, BindingResult bindingResult) throws Exception {
        if (!registerBean.isValid()) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("Invalid input format detected")
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        UserEntity user = UserEntity.builder()
                .emailAddress(registerBean.getEmailAddress())
                .pin(registerBean.getPin())
                .mobileNumber(registerBean.getMobileNumber())
                .build();
        UserEntity newUser = null;
        try {
           newUser= userEntityService.save(user);
        } catch(Exception e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/test")
    public String test() {
        return "String";
    }
}
