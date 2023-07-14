package com.champ.nocash;
import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.LoginCounter;
import com.champ.nocash.collection.Salt;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


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

    // ... existing test methods ...

    @Test
    void save_ValidUserEntity_ReturnsSavedEntity() throws Exception {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmailAddress("test@example.com");
        userEntity.setMobileNumber("1234567890");
        userEntity.setPin("password");

        when(userEntityRepository.findFirstByEmailAddress(userEntity.getEmailAddress())).thenReturn(null);
        when(userEntityRepository.findFirstByMobileNumber(userEntity.getMobileNumber())).thenReturn(null);
        when(passwordEncoder.encode(userEntity.getPin())).thenReturn("password");
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        UserEntity savedEntity = userEntityService.save(userEntity);

        // Assert
        Assertions.assertEquals(userEntity, savedEntity);
        Assertions.assertEquals("password", savedEntity.getPin());
        verify(userEntityRepository, times(1)).findFirstByEmailAddress(userEntity.getEmailAddress());
        verify(userEntityRepository, times(1)).findFirstByMobileNumber(userEntity.getMobileNumber());
        verify(passwordEncoder, times(1)).encode(userEntity.getPin());
        verify(userEntityRepository, times(1)).save(userEntity);
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
    void login_ValidCredentials_ReturnsAuthenticationResponse() throws Exception {
        // Arrange
        String mobileNumber = "1234567890";
        String pin = "password";
        String ipAddress = "192.168.0.1";
        String userAgent = "Chrome";

        UserEntity userEntity = new UserEntity();
        userEntity.setId("user123");
        userEntity.setMobileNumber(mobileNumber);
        userEntity.setPin(passwordEncoder.encode(pin));
        userEntity.setIsLocked(false);
        userEntity.setEmailAddress("test@example.com");
        userEntity.setSalt(new Salt());
        userEntity.setLoginCounter(new LoginCounter());

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setMobileNumber(mobileNumber);
        authenticationRequest.setPin(pin);

        when(userEntityRepository.findFirstByMobileNumber(mobileNumber)).thenReturn(userEntity);
        when(customUserDetailService.loadUserByUsername(mobileNumber)).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any(UserDetails.class), eq(ipAddress), eq(userAgent))).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = userEntityService.login(authenticationRequest, ipAddress, userAgent);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Jon", response.getFirstName());
        Assertions.assertEquals("Narva", response.getLastName());
        Assertions.assertEquals("test@example.com", response.getEmailAddress());
        Assertions.assertEquals(mobileNumber, response.getMobileNumber());
        Assertions.assertEquals("user123", response.getUserID());
        Assertions.assertEquals("jwtToken", response.getJwt());

        verify(userEntityRepository, times(1)).findFirstByMobileNumber(mobileNumber);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authenticationHistoryService, times(1)).save(any(AuthenticationHistoryEntity.class));
        verify(userEntityRepository, times(1)).save(any(UserEntity.class));
        verify(jwtUtil, times(1)).generateToken(any(UserDetails.class), eq(ipAddress), eq(userAgent));
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
