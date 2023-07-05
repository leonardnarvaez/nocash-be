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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CardEntityServiceImpl implements CardEntityService {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public CardEntity save(CardEntity cardEntity) {
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
    public void deleteCard(String id) {
        UserEntity user = securityUtil.getUserEntity();
        if (user != null) {
            List<CardEntity> cards = user.getCards();
            if (cards != null) {
                cards.removeIf(card -> card.getId().equals(id));
                try {
                    userEntityService.updateUser(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new NoSuchElementException("User does not have any cards");
            }
        } else {
            throw new NoSuchElementException("User not found");
        }
    }
}
