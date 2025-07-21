package com.lms.application.usecases.user;

import com.lms.application.dto.request.CreateUserRequest;
import com.lms.application.dto.response.UserResponse;
import com.lms.domain.entities.User;
import com.lms.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .build();
        user.setRole(request.getRole()); // Set role using the setter
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
}
