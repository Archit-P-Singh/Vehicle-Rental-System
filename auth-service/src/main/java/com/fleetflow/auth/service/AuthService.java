package com.fleetflow.auth.service;

import com.fleetflow.auth.domain.entity.User;
import com.fleetflow.auth.domain.enums.AccountStatus;
import com.fleetflow.auth.domain.enums.Role;
import com.fleetflow.auth.dto.AuthResponse;
import com.fleetflow.auth.dto.LoginRequest;
import com.fleetflow.auth.dto.RegisterRequest;
import com.fleetflow.auth.repository.UserRepository;
import com.fleetflow.auth.security.CustomUserDetails;
import com.fleetflow.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER) // Default role for registration
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        userRepository.save(user);

        var customUserDetails = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(customUserDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var customUserDetails = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(customUserDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
