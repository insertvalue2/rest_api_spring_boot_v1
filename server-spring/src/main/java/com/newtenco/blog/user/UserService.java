package com.newtenco.blog.user;

import com.newtenco.blog.core.handler.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse.DTO 회원가입(UserRequest.JoinDTO reqDTO) {
        if (userRepository.existsByUsername(reqDTO.username())) {
            throw new BadRequestException("이미 존재하는 유저네임입니다");
        }
        String encPassword = passwordEncoder.encode(reqDTO.password());
        User savedUser = userRepository.save(reqDTO.toEntity(encPassword));
        return new UserResponse.DTO(savedUser);
    }
}
