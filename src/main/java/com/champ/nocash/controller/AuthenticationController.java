package com.champ.nocash.controller;

import com.champ.nocash.bean.RegisterBean;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.security.CustomUserDetailService;
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

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserEntityService userEntityService;
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getMobileNumber(), authenticationRequest.getPin()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Not Found")
                    .message("User not found")
                    .status(404)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(authenticationRequest.getMobileNumber());
        final String jwt = jwtUtil.generateToken(userDetails);
        final UserEntity userEntity = userEntityService.findUserByMobile(userDetails.getUsername());
        return ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .firstName("Jon")
                        .lastName("Narva")
                        .emailAddress(userEntity.getEmailAddress())
                        .mobileNumber(userEntity.getMobileNumber())
                        .jwt(jwt)
                        .userID(userEntity.getId())
                        .build()
        );
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
