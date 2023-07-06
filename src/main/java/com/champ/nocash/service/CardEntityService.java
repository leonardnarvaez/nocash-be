package com.champ.nocash.service;

import com.champ.nocash.collection.CardEntity;

import java.util.List;

public interface CardEntityService {
    CardEntity save(CardEntity cardEntity);

    List<CardEntity> findAllCards();

    CardEntity findCardById(String id);

    void deleteCard(String id);
}
