package com.champ.nocash.controller;

import com.champ.nocash.bean.CardBean;
import com.champ.nocash.collection.CardEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.CardEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.util.NumberGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.Card;
import java.util.List;
import java.util.Random;

import static com.champ.nocash.util.ColorGeneratorUtil.getRandomColor;

@RestController
@RequestMapping("/api/card")
public class CardController {
    @Autowired
    private CardEntityService cardEntityService;
    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/")
    public ResponseEntity<?> saveCard(@RequestBody CardBean cardBean) {
        if (!cardBean.isAccountNumberValid() && !cardBean.isCvvValid()) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("Invalid input format detected")
                    .status(401)
                    .path("/api/card/create")
                    .build(), HttpStatus.BAD_REQUEST);
            }
            CardEntity cardEntity = CardEntity.builder()
                    .accountNumber(cardBean.getAccountNumber())
                    .expiryDate(cardBean.getExpiryDate())
                    .cvv(cardBean.getCvv())
                    .name(cardBean.getName())
                    .color(getRandomColor())
                    .build();
            CardEntity newCard = null;
            try {
                newCard = cardEntityService.save(cardEntity);
            } catch (Exception e) {
                return new ResponseEntity(ErrorResponse.builder()
                        .error("Bad Request1")
                        .message(e.getMessage())
                        .status(401)
                        .path("/api/card/create")
                        .build(), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(newCard);
        }

    @GetMapping("/")
    public List<CardEntity> getCardsForCurrentUser() {
        return cardEntityService.findAllCards();
    }

    @DeleteMapping("/{cardId}")
    public void deleteCardForCurrentUser(@PathVariable String cardId) {
        cardEntityService.deleteCard(cardId);
    }
}

