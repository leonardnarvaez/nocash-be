package com.champ.nocash;
import com.champ.nocash.collection.*;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.WalletTransactionService;
import com.champ.nocash.service.impl.UserEntityServiceImpl;
import com.champ.nocash.util.EmailService;
import com.champ.nocash.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserEntityServiceTest {
    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private WalletTransactionService walletTransactionService;
    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationHistoryService authenticationHistoryService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private UserEntityServiceImpl userEntityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_DuplicateEmail_ThrowsException() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmailAddress("test@example.com");

        when(userEntityRepository.findFirstByEmailAddress(userEntity.getEmailAddress())).thenReturn(userEntity);

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> userEntityService.save(userEntity));
        verify(userEntityRepository, times(1)).findFirstByEmailAddress(userEntity.getEmailAddress());
        verify(userEntityRepository, never()).findFirstByMobileNumber(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void findUserByMobile_ValidMobile_ReturnsUserEntity() {
        // Arrange
        String mobileNumber = "1234567890";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setMobileNumber(mobileNumber);
        when(userEntityRepository.findFirstByMobileNumber(mobileNumber)).thenReturn(expectedUser);

        // Act
        UserEntity actualUser = userEntityService.findUserByMobile(mobileNumber);

        // Assert
        Assertions.assertEquals(expectedUser, actualUser);
        verify(userEntityRepository, times(1)).findFirstByMobileNumber(mobileNumber);
    }
    @Test
    public void testSave() throws Exception {
        // Arrange
        UserEntity user = new UserEntity();
        when(userEntityRepository.save(user)).thenReturn(user);

        // Act
        UserEntity result = userEntityService.save(user);

        // Assert
        Assertions.assertEquals(user, result);
        verify(userEntityRepository, times(1)).save(user);
    }

    @Test
    public void testFindUserByUsername() {
        // Arrange
        String username = "testuser";
        UserEntity expectedUser = new UserEntity();
        when(userEntityRepository.findFirstByUsername(username)).thenReturn(expectedUser);

        // Act
        UserEntity result = userEntityService.findUserByUsername(username);

        // Assert
        Assertions.assertEquals(expectedUser, result);
        verify(userEntityRepository, times(1)).findFirstByUsername(username);
    }

    @Test
    void findUserByMobile_MobileNotFound_ReturnsNull() {
        // Arrange
        String mobileNumber = "1234567890";
        when(userEntityRepository.findFirstByMobileNumber(mobileNumber)).thenReturn(null);

        // Act
        UserEntity actualUser = userEntityService.findUserByMobile(mobileNumber);

        // Assert
        Assertions.assertNull(actualUser);
        verify(userEntityRepository, times(1)).findFirstByMobileNumber(mobileNumber);
    }

    @Test
    void findUserByEmail_ValidEmail_ReturnsUserEntity() {
        // Arrange
        String email = "test@example.com";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setEmailAddress(email);
        when(userEntityRepository.findFirstByEmailAddress(email)).thenReturn(expectedUser);

        // Act
        UserEntity actualUser = userEntityService.findUserByEmail(email);

        // Assert
        Assertions.assertEquals(expectedUser, actualUser);
        verify(userEntityRepository, times(1)).findFirstByEmailAddress(email);
    }

    @Test
    void findUserByEmail_EmailNotFound_ReturnsNull() {
        // Arrange
        String email = "test@example.com";
        when(userEntityRepository.findFirstByEmailAddress(email)).thenReturn(null);

        // Act
        UserEntity actualUser = userEntityService.findUserByEmail(email);

        // Assert
        Assertions.assertNull(actualUser);
        verify(userEntityRepository, times(1)).findFirstByEmailAddress(email);
    }

    @Test
    void updateUser_ValidUserEntity_ReturnsUpdatedUserEntity() throws Exception {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId("123");
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        UserEntity updatedUser = userEntityService.updateUser(userEntity);

        // Assert
        Assertions.assertEquals(userEntity, updatedUser);
        verify(userEntityRepository, times(1)).save(userEntity);
    }


    @Test
    void updatePIN_ValidOldPINAndNewPIN_UpdatesPIN() throws Exception {
        // Arrange
        String oldPIN = "oldPIN";
        String newPIN = "newPIN";
        UserEntity userEntity = new UserEntity();
        userEntity.setPin("encodedOldPIN");

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(passwordEncoder.matches(any(CharSequence.class), any(String.class))).thenReturn(true);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedNewPIN");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        userEntityService.updatePIN(oldPIN, newPIN);

        // Assert
        Assertions.assertEquals("encodedNewPIN", userEntity.getPin());
        verify(securityUtil, times(1)).getUserEntity();
        verify(passwordEncoder, times(1)).matches(eq(oldPIN), any(String.class));
        verify(passwordEncoder, times(1)).encode(eq(newPIN));
        verify(userEntityRepository, times(1)).save(eq(userEntity));
    }

    @Test
    void updatePIN_InvalidOldPIN_ThrowsException() {
        // Arrange
        String oldPIN = "wrongOldPIN";
        String newPIN = "newPIN";
        UserEntity userEntity = new UserEntity();
        userEntity.setPin("encodedOldPIN");

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(passwordEncoder.matches(any(CharSequence.class), any(String.class))).thenReturn(false);

        // Act and Assert
        Assertions.assertThrows(Exception.class, () -> {
            userEntityService.updatePIN(oldPIN, newPIN);
        });

        // Verify
        verify(securityUtil, times(1)).getUserEntity();
        verify(passwordEncoder, times(1)).matches(eq(oldPIN), any(String.class));
        verify(passwordEncoder, never()).encode(any(CharSequence.class));
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testFindUserById() {
        // Arrange
        String userId = "12345";
        UserEntity expectedUser = new UserEntity();
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<UserEntity> result = userEntityService.findUserById(userId);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedUser, result.get());
        verify(userEntityRepository, times(1)).findById(userId);
    }
    @Test
    void validatePIN_ValidPIN_ReturnsTrue() {
        // Arrange
        String pin = "PIN";
        UserEntity userEntity = new UserEntity();
        userEntity.setPin("encodedPIN");

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(passwordEncoder.matches(pin, userEntity.getPin())).thenReturn(true);

        // Act
        boolean isValid = userEntityService.validatePIN(pin);

        // Assert
        Assertions.assertTrue(isValid);
        verify(securityUtil, times(1)).getUserEntity();
        verify(passwordEncoder, times(1)).matches(pin, userEntity.getPin());
    }

    @Test
    void validatePIN_InvalidPIN_ReturnsFalse() {
        // Arrange
        String pin = "invalidPIN";
        UserEntity userEntity = new UserEntity();
        userEntity.setPin("validPIN");

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(passwordEncoder.matches(eq(pin), eq(userEntity.getPin()))).thenReturn(false);

        // Act
        boolean isValid = userEntityService.validatePIN(pin);

        // Assert
        Assertions.assertFalse(isValid);
        verify(securityUtil, times(1)).getUserEntity();
        verify(passwordEncoder, times(1)).matches(eq(pin), eq(userEntity.getPin()));
    }
}



//    @Test
//    void login_InvalidCredentials_ThrowsAuthenticationException() throws Exception {
//        // Arrange
//        String mobileNumber = "1234567890";
//        String pin = "wrongpassword";
//        String ipAddress = "192.168.0.1";
//        String userAgent = "Chrome";
//
//        UserEntity userEntity = new UserEntity();
//        userEntity.setId("user123");
//        userEntity.setMobileNumber(mobileNumber);
//        userEntity.setPin(passwordEncoder.encode("password")); // Different password than what is provided
//
//        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
//        authenticationRequest.setMobileNumber(mobileNumber);
//        authenticationRequest.setPin(pin);
//
//        when(userEntityRepository.findFirstByMobileNumber(mobileNumber)).thenReturn(userEntity);
//        doThrow(BadCredentialsException.class).when(authenticationManager)
//                .authenticate(any(UsernamePasswordAuthenticationToken.class));
//
//        // Act and Assert
//        Assertions.assertThrows(AuthenticationException.class, () -> {
//            userEntityService.login(authenticationRequest, ipAddress, userAgent);
//        });
//
//        verify(userEntityRepository, times(1)).findFirstByMobileNumber(mobileNumber);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(authenticationHistoryService, never()).save(any(AuthenticationHistoryEntity.class));
//        verify(userEntityRepository, never()).save(any(UserEntity.class));
//        verify(jwtUtil, never()).generateToken(any(UserDetails.class), eq(ipAddress), eq(userAgent));
//    }
