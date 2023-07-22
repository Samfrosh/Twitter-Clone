package com.samuel.service;

import com.samuel.dto.request.RegistrationRequest;
import com.samuel.enums.Role;
import com.samuel.model.User;
import com.samuel.repository.UserMetadataRepository;
import com.samuel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @MockBean private UserRepository userRepository;
    private RegistrationService registrationService;
    @MockBean private UserMetadataRepository userMetadataRepository;
    @MockBean private IsEmailValid isemailValid;
    @MockBean private EmailService emailService;
    @MockBean private ConfirmationTokenService confirmationTokenService;
    @MockBean private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(
                userRepository, userMetadataRepository,
                isemailValid, emailService,confirmationTokenService,passwordEncoder
        );
    }

    @Test
    void registration_SuccessfulRegistration_ReturnsToken() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        RegistrationRequest registrationRequest = new RegistrationRequest(
                "Akinyemi samuel",
                "samuel@gmail.com",
                "19-09-2002"
        );

        when(userRepository.findByEmail(registrationRequest.email())).thenReturn(Optional.empty());
        when(isemailValid.test(registrationRequest.email())).thenReturn(true);

        User user = User.builder()
                .userId(100L)
                .fullname(registrationRequest.fullName())
                .email(registrationRequest.email())
                .role(Role.USER)
                .registrationDate(LocalDateTime.now())
                .build();
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        String token = "registration_token";
        //when(confirmationTokenService.createConfirmationToken(user)).thenReturn(token);
        doReturn(token).when(confirmationTokenService).createConfirmationToken(any());

        // When
        String result = registrationService.registration(registrationRequest, httpServletRequest);

        // Then
        assertEquals(token, result);

        // Ensure that userRepository.findByEmail is called once
        verify(userRepository, times(1)).findByEmail(registrationRequest.email());
        // Ensure that emailService.send is called once
        verify(emailService, times(1)).send(anyString(), anyString());
        // Ensure that userRepository.saveAndFlush is called once
        verify(userRepository, times(1)).saveAndFlush(any());
        // Ensure that userMetadataRepository.save is called once
        verify(userMetadataRepository, times(1)).save(any());
        // Ensure that confirmationTokenService.createConfirmationToken is called once
        verify(confirmationTokenService, times(1)).createConfirmationToken(user);

    }

    @Test
    void passwordRegistration() {
    }
}