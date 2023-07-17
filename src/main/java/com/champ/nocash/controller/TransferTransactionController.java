package com.champ.nocash.controller;

import com.champ.nocash.bean.MoneyTransferBean;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.TransferTransactionService;
import com.champ.nocash.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/money-transfer")
public class TransferTransactionController {
    @Autowired
    private TransferTransactionService transferTransactionService;
    @Autowired
    private UserEntityService userEntityService;

    @PostMapping("/transfer")
    public ResponseEntity<?> trasfer(@RequestBody MoneyTransferBean bean) {
        try {
            transferTransactionService.transfer(bean.getMobileNumber(), BigDecimal.valueOf(bean.getAmount()), bean.getPin());
        } catch (Exception e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/api/money-transfer/transer")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new HashMap<String, String>(){{
            put("message", "Transfer success!");
        }});
    }

    @PostMapping("/you-there")
    public ResponseEntity<?> youThere(@RequestBody MoneyTransferBean bean) {
        UserEntity user = userEntityService.findUserByMobile(bean.getMobileNumber());
        if(user == null || !user.getIsActive() || user.getIsLocked()) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("User is not there")
                    .status(401)
                    .path("/api/money-transfer/you-there")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        String obfuscatedUsername = "";
        String username = user.getUsername();
        if(username.length() >= 3) {
            obfuscatedUsername += username.substring(0,2);
            for(int i = 2; i < username.length() - 1; i++) {
                char currentChar = username.charAt(i);
                if(currentChar == ' ') {
                    obfuscatedUsername += ' ';
                } else {
                    obfuscatedUsername += '*';
                }
            }
            obfuscatedUsername += username.charAt(username.length() - 1);
        }
        Map<String, String> respMap = new HashMap<>();
        respMap.put("mobileNumber", bean.getMobileNumber());
        respMap.put("username", obfuscatedUsername);
        return ResponseEntity.ok(respMap);
    }

}
