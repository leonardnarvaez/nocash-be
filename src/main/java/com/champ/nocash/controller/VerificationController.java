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

import java.util.HashMap;

@RestController
@RequestMapping("/api/security")
public class VerificationController {
    @Autowired
    private VerificationService verificationService;
    @PostMapping("/pin-reset")
    public ResponseEntity<?> pinReset(@RequestBody PinResetBean pinResetBean) {
        try {
            verificationService.pinReset(pinResetBean.getOldPIN(), pinResetBean.getNewPIN());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Failed to reset pin")
                    .message(e.getMessage())
                    .status(404)
                    .path("/api/pin-reset")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "pin reset success");
        }});
    }
}
