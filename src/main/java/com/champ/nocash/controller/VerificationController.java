package com.champ.nocash.controller;

import com.champ.nocash.bean.PinResetBean;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/api/security")
public class VerificationController {
    @Autowired
    private VerificationService verificationService;
    @PostMapping("/pin-reset")
    public ResponseEntity<?> pinReset(@RequestBody PinResetBean pinResetBean, HttpServletRequest request) {
        if(pinResetBean.getOldPIN().equals(pinResetBean.getNewPIN())) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("PIN Error")
                    .message("New Pin and Old pin must not match")
                    .status(404)
                    .path("/api/pin-reset")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        try {
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            verificationService.pinReset(pinResetBean.getOldPIN(), pinResetBean.getNewPIN(), ipAddress, userAgent);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error(e.getMessage())
                    .message(e.getMessage())
                    .status(404)
                    .path("/api/pin-reset")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "PIN reset success");
        }});
    }
}
