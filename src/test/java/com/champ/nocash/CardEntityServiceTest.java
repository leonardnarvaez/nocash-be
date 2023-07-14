package com.champ.nocash;

import com.champ.nocash.collection.CardEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.impl.CardEntityServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class CardEntityServiceTest {
    @Mock
    private UserEntityService userEntityService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CardEntityServiceImpl cardEntityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ValidCardEntity_ReturnsSavedCardEntity() throws Exception {
        CardEntity cardEntity = new CardEntity();
        cardEntity.setAccountNumber("1234567890");

        UserEntity user = new UserEntity();
        user.setCards(new ArrayList<>());

        when(securityUtil.getUserEntity()).thenReturn(user);
        when(userEntityService.updateUser(user)).thenReturn(user);

        CardEntity savedCardEntity = cardEntityService.save(cardEntity);

        Assertions.assertEquals(cardEntity, savedCardEntity);
        Assertions.assertTrue(user.getCards().contains(cardEntity));
        verify(userEntityService, times(1)).updateUser(user);
    }

    @Test
    void save_DuplicateCardNumber_ThrowsException() throws Exception {
        CardEntity cardEntity = new CardEntity();
        cardEntity.setAccountNumber("1234567890");

        UserEntity user = new UserEntity();
        CardEntity existingCard = new CardEntity();
        existingCard.setAccountNumber("1234567890");
        List<CardEntity> cards = new ArrayList<>();
        cards.add(existingCard);
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        Assertions.assertThrows(Exception.class, () -> cardEntityService.save(cardEntity));
        verify(userEntityService, never()).updateUser(user);
    }

    @Test
    void findAllCards_UserFound_ReturnsListOfCards() {
        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        cards.add(new CardEntity());
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        List<CardEntity> result = cardEntityService.findAllCards();

        Assertions.assertEquals(cards, result);
    }

    @Test
    void findAllCards_UserNotFound_ThrowsException() {
        when(securityUtil.getUserEntity()).thenReturn(null);

        Assertions.assertThrows(NoSuchElementException.class, () -> cardEntityService.findAllCards());
    }

    @Test
    void findCardByCardId_CardFound_ReturnsCardEntity() {
        String cardId = "1234567890";
        CardEntity cardEntity = new CardEntity();
        cardEntity.setAccountNumber(cardId);

        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        cards.add(cardEntity);
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        CardEntity result = cardEntityService.findCardByCardId(cardId);

        Assertions.assertEquals(cardEntity, result);
    }

    @Test
    void findCardByCardId_CardNotFound_ThrowsException() {
        String cardId = "1234567890";
        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        Assertions.assertThrows(NoSuchElementException.class, () -> cardEntityService.findCardByCardId(cardId));
    }

    @Test
    void findCardById_CardFound_ReturnsCardEntity() {
        String cardId = "123";
        CardEntity cardEntity = new CardEntity();
        cardEntity.setId(cardId);

        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        cards.add(cardEntity);
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        CardEntity result = cardEntityService.findCardById(cardId);

        Assertions.assertEquals(cardEntity, result);
    }

    @Test
    void findCardById_CardNotFound_ThrowsException() {
        String cardId = "123";
        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        Assertions.assertThrows(NoSuchElementException.class, () -> cardEntityService.findCardById(cardId));
    }

    @Test
    void deleteCard_CardExists_DeletesCard() throws Exception {
        String cardId = "123";
        CardEntity cardEntity = new CardEntity();
        cardEntity.setId(cardId);

        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        cards.add(cardEntity);
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);
        when(userEntityService.updateUser(user)).thenReturn(user);

        cardEntityService.deleteCard(cardId);

        Assertions.assertFalse(user.getCards().contains(cardEntity));
        verify(userEntityService, times(1)).updateUser(user);
    }

    @Test
    void deleteCard_CardDoesNotExist_ThrowsException() throws Exception {
        String cardId = "123";
        UserEntity user = new UserEntity();
        List<CardEntity> cards = new ArrayList<>();
        user.setCards(cards);

        when(securityUtil.getUserEntity()).thenReturn(user);

        Assertions.assertThrows(NoSuchElementException.class, () -> cardEntityService.deleteCard(cardId));
        verify(userEntityService, never()).updateUser(user);
    }
}
