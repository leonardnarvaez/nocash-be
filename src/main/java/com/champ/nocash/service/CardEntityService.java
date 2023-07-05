package com.champ.nocash.service;

import com.champ.nocash.collection.CardEntity;

import java.util.List;

public interface CardEntityService {
    CardEntity save(CardEntity cardEntity);

    List<CardEntity> findAllCards();

    void deleteCard(String id);
}
