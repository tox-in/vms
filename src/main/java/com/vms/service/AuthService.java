package com.vms.service;

import com.vms.dto.request.LoginRequest;
import com.vms.dto.request.SignUpRequest;
import com.vms.dto.response.AuthResponse;
import com.vms.model.User;
import com.vms.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Authenticates a user with email and password
     * @param loginRequest containing email and password
     * @return AuthResponse with JWT token and user details
     * @throws RuntimeException if user is not found
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        // Fetch user details
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.email()));

        return new AuthResponse(jwt, user.getEmail(), user.getRole());
    }

    /**
     * Registers a new user in the system
     * @param signUpRequest containing user registration details
     * @return AuthResponse with JWT token and user details
     * @throws RuntimeException if email is already registered
     * @throws RuntimeException if national ID is already registered
     */
    public AuthResponse registerUser(SignUpRequest signUpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new RuntimeException("Email is already registered: " + signUpRequest.email());
        }

        // Check if national ID already exists
        if (userRepository.existsByNationalId(signUpRequest.nationalId())) {
            throw new RuntimeException("National ID is already registered: " + signUpRequest.nationalId());
        }

        // Create new user
        User user = createUserFromSignUpRequest(signUpRequest);

        // Save user to database(timestamps will be set by @PrePersist)
        User savedUser = userRepository.save(user);

        // Create authentication token for the new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.email(),
                        signUpRequest.password()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(jwt, savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * Validates if an email is available for registration
     * @param email to check
     * @return true if email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Validates if a national ID is available for registration
     * @param nationalId to check
     * @return true if national ID is available, false otherwise
     */
    public boolean isNationalIdAvailable(String nationalId) {
        return !userRepository.existsByNationalId(nationalId);
    }

    /**
     * Refreshes JWT token for authenticated user
     * @param authentication current authentication
     * @return new JWT token
     */
    public String refreshToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    /**
     * Logs out user by invalidating the current authentication
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Gets current authenticated user
     * @return User object of currently authenticated user
     * @throws RuntimeException if user is not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Helper method to create User entity from SignUpRequest
     */
    private User createUserFromSignUpRequest(SignUpRequest signUpRequest) {
        return new User(
                signUpRequest.name(),
                signUpRequest.email(),
                signUpRequest.phone(),
                signUpRequest.nationalId(),
                passwordEncoder.encode(signUpRequest.password()),
                signUpRequest.role()
        );
    }
}