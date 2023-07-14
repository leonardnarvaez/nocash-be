package com.champ.nocash.service.impl;

import com.champ.nocash.collection.CardEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.CardEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.util.NumberGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CardEntityServiceImpl implements CardEntityService {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public CardEntity save(CardEntity cardEntity) throws Exception {
        boolean isCardFound = false;
        try {
            findCardByCardId(cardEntity.getAccountNumber());
            isCardFound = true;
        } catch (NoSuchElementException e) {
            isCardFound = false;
        }
        if(isCardFound) {
            throw new Exception("Account number already exists.");
        }
        UserEntity user = securityUtil.getUserEntity();
        cardEntity.setId(NumberGeneratorUtil.generateRandomId());
        if(user.getCards() != null) {
            user.getCards().add(cardEntity);
        } else {
            user.setCards(new ArrayList<>());
        }

        try {
            userEntityService.updateUser(user);
            return cardEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CardEntity> findAllCards() {
        UserEntity user = securityUtil.getUserEntity();
        if (user != null) {
            return user.getCards();
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    @Override
    public CardEntity findCardByCardId(String cardId) {
        UserEntity user = securityUtil.getUserEntity();
        if (user != null) {
            List<CardEntity> cards = user.getCards();
            if (cards != null) {
                Optional<CardEntity> retrievedCard = cards.stream().filter(card -> card.getAccountNumber().equals(cardId)).findFirst();
                if (retrievedCard.isPresent()) {
                    return retrievedCard.get();
                }
            }
        } else {
            throw new NoSuchElementException("User not found");
        }
        throw new NoSuchElementException("Card not found");
    }

    @Override
    public CardEntity findCardById(String id) {
        UserEntity user = securityUtil.getUserEntity();
        if (user != null) {
            List<CardEntity> cards = user.getCards();
            if (cards != null) {
                Optional<CardEntity> retrievedCard = cards.stream().filter(card -> card.getId().equals(id)).findFirst();
                if (retrievedCard.isPresent()) {
                    return retrievedCard.get();
                }
            }
        } else {
            throw new NoSuchElementException("User not found");
        }
        throw new NoSuchElementException("Card not found");
    }


    @Override
    public void deleteCard(String id) {
        UserEntity user = securityUtil.getUserEntity();
        if (user != null) {
            List<CardEntity> cards = user.getCards();
            if (cards != null) {
                boolean removed = cards.removeIf(card -> card.getId().equals(id));
                if (removed) {
                    try {
                        userEntityService.updateUser(user);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new NoSuchElementException("Card not found");
                }
            } else {
                throw new NoSuchElementException("User does not have any cards");
            }
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

}
