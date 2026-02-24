package com.himaloyit.buildnation.sac.services.impl;

import com.himaloyit.buildnation.sac.domain.dto.UserDTO;
import com.himaloyit.buildnation.sac.domain.entities.User;
import com.himaloyit.buildnation.sac.domain.enums.UserRole;
import com.himaloyit.buildnation.sac.domain.mapper.IUserMapper;
import com.himaloyit.buildnation.sac.domain.model.AuthResponse;
import com.himaloyit.buildnation.sac.domain.model.LoginRequest;
import com.himaloyit.buildnation.sac.domain.model.RegisterRequest;
import com.himaloyit.buildnation.sac.domain.repositories.iRepositories.IUserRepository;
import com.himaloyit.buildnation.sac.security.JwtTokenProvider;
import com.himaloyit.buildnation.sac.services.iServices.IAuthService;
import com.himaloyit.buildnation.sac.util.exceptions.InvalidTokenException;
import com.himaloyit.buildnation.sac.util.exceptions.UserAlreadyExistsException;
import com.himaloyit.buildnation.sac.util.exceptions.UserNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(IUserRepository userRepository,
                           IUserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.VIEWER;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        UserDTO userDTO = userMapper.toDto(savedUser);

        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getEmail(), savedUser.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getEmail()));

        UserDTO userDTO = userMapper.toDto(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromRefreshToken(refreshToken);
        if (email == null) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        // Rotate: delete old, issue new
        jwtTokenProvider.deleteRefreshToken(refreshToken);

        UserDTO userDTO = userMapper.toDto(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(3600L)
                .user(userDTO)
                .build();
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        jwtTokenProvider.blacklistAccessToken(accessToken);
        if (refreshToken != null && !refreshToken.isBlank()) {
            jwtTokenProvider.deleteRefreshToken(refreshToken);
        }
    }

    @Override
    public UserDTO validateToken(String accessToken) {
        if (!jwtTokenProvider.isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("Token is invalid or has been revoked");
        }
        String email = jwtTokenProvider.getEmailFromToken(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return userMapper.toDto(user);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return userMapper.toDto(user);
    }
}
