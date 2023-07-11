package com.champ.nocash.controller;

import com.champ.nocash.bean.BillPaymentBean;
import com.champ.nocash.bean.MerchantBean;
import com.champ.nocash.collection.MerchantEntity;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.BillPaymentService;
import com.champ.nocash.service.MerchantEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantEntityService merchantEntityService;

    @Autowired
    private BillPaymentService billPaymentService;

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody MerchantBean merchantBean) throws Exception {
        MerchantEntity merchant = MerchantEntity.builder()
                .merchantId(merchantBean.getMerchantId())
                .name(merchantBean.getName())
                .imagePath(merchantBean.getImagePath())
                .build();
        MerchantEntity newMerchantEntity = null;
        try {
            newMerchantEntity = merchantEntityService.save(merchant);
        } catch(Exception e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(newMerchantEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@RequestBody MerchantBean merchantBean, @PathVariable String id) throws Exception {
        Optional<MerchantEntity> optionalMerchant = merchantEntityService.findById(id);
        if (optionalMerchant.isPresent()) {
            return ResponseEntity.ok(optionalMerchant.get());
        } else {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Merchant Not Found")
                    .message("Merchant ID :" + id + "not registered in the database")
                    .status(404)
                    .path("/api/merchant/"+id)
                    .build(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findAll() throws Exception {
        return ResponseEntity.ok(merchantEntityService.findAll());
    }

    @PostMapping("/payment")
    public ResponseEntity<?> pay(@RequestBody BillPaymentBean billPaymentBean) {
            try {
                billPaymentService.payBill(BigDecimal.valueOf(billPaymentBean.getAmount()), billPaymentBean.getMerchantId(), billPaymentBean.getAccountNumber());
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
    }

