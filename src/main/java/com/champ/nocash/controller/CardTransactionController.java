package com.champ.nocash.controller;

import com.champ.nocash.bean.CardTransactionBean;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.CardTransactionService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/card-transaction")
public class CardTransactionController {

    @Autowired
    private WalletTransactionService walletTransactionService;
    @Autowired
    private CardTransactionService cardTransactionService;

    @PostMapping("/cash-in")
    public ResponseEntity<?> cashIn(@RequestBody CardTransactionBean cardTransactionBean) throws Exception {
        boolean isSuccess = cardTransactionService.cashIn(BigDecimal.valueOf(cardTransactionBean.getAmount()), cardTransactionBean.getCardId(), cardTransactionBean.getPin());
        if (isSuccess) {
            return ResponseEntity.ok(new HashMap<String, String>(){{
                put("message", "transaction complete");
            }});
        } else {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("Transaction Failed")
                    .status(401)
                    .path("/api/card0")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cash-out")
    public ResponseEntity<?> cashOut(@RequestBody CardTransactionBean cardTransactionBean) {
        try {
            cardTransactionService.cashOut(BigDecimal.valueOf(cardTransactionBean.getAmount()), cardTransactionBean.getCardId(), cardTransactionBean.getPin());
            return ResponseEntity.ok(new HashMap<String, String>(){{
                put("message", "transaction complete");
            }});
        } catch (Exception e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        return ResponseEntity.ok(walletTransactionService.getBalance());
    }
}
