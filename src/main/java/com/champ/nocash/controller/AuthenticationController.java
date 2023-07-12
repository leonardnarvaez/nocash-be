package com.champ.nocash.controller;

import com.champ.nocash.bean.EmailVerificationRequestBean;
import com.champ.nocash.bean.ReactivateAccountBean;
import com.champ.nocash.bean.RegisterBean;
import com.champ.nocash.bean.VerifyEmailBean;
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
import com.champ.nocash.service.VerificationService;
import com.champ.nocash.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

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
    @Autowired
    private VerificationService verificationService;
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) throws Exception {
        AuthenticationResponse authenticationResponse = null;
        try {
            authenticationResponse = userEntityService.login(authenticationRequest, httpServletRequest.getRemoteAddr(), httpServletRequest.getHeader("User-Agent"));
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
                    .message("Invalid User Credentials")
                    .status(500)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) throws Exception {
        UserEntity loggedInUser = securityUtil.getUserEntity();
        if(loggedInUser != null) {
            // there is a logged in user
            AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                    .userId(loggedInUser.getId())
                    .isAuthenticationResultSuccess(true)
                    .authenticationType(AuthenticationType.LOGOUT)
                    .userAgent(httpServletRequest.getHeader("User-Agent"))
                    .ipAddress(httpServletRequest.getRemoteAddr())
                    .build();
            authenticationHistoryService.save(authenticationHistoryEntity);
            loggedInUser.getSalt().refreshSalt();
            userEntityService.updateUser(loggedInUser);
            return ResponseEntity.ok(new HashMap<String, String>(){{
                put("message", "logout success");
            }});
        } else {
            return ResponseEntity.ok(new HashMap<String, String>(){{
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
//        verificationService.requestEmailVerification(user.getEmailAddress());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/request-email-verification")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerificationRequestBean emailVerificationBean) {
        try {
            verificationService.requestEmailVerification(emailVerificationBean.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "verification email has been sent");
        }});
    }
    @PostMapping("/request-account-reactivation")
    public ResponseEntity<?> reactivateAccount(@RequestBody EmailVerificationRequestBean emailVerificationBean) {
        try {
            verificationService.requestAccountReactivation(emailVerificationBean.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "reactivation email has been sent");
        }});
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailBean verifyEmailBean) {
        try {
            verificationService.verifyEmail(verifyEmailBean.getEmail(), verifyEmailBean.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "email successfully verified");
        }});
    }

    @PostMapping("/reactivate-account")
    public ResponseEntity<?> reactivateAccount(@RequestBody ReactivateAccountBean reactivateAccountBean, HttpServletRequest httpServletRequest) {
        try {
            verificationService.reactivateAccount(reactivateAccountBean.getEmail(), reactivateAccountBean.getCode(), reactivateAccountBean.getNewPIN(), httpServletRequest.getRemoteAddr(), httpServletRequest.getHeader("User-Agent"));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "account successfully reactivated");
        }});
    }

    @GetMapping("/test")
    public String test() {
        return "String";
    }
}
