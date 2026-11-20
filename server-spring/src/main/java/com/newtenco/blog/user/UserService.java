package com.newtenco.blog.user;

import com.newtenco.blog.core.handler.errors.BadRequestException;
import com.newtenco.blog.core.handler.errors.UnauthorizedException;
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

    public UserResponse.DTO 로그인(UserRequest.LoginDTO reqDTO) {
        User user = userRepository.findByUsername(reqDTO.username())
                .orElseThrow(() -> new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다"));

        if (!passwordEncoder.matches(reqDTO.password(), user.getPassword())) {
            throw new UnauthorizedException("유저네임 혹은 비밀번호가 일치하지 않습니다");
        }
        return new UserResponse.DTO(user);
    }

    public UserResponse.AvailableDTO 유저네임중복체크(String username) {
        boolean available = !userRepository.existsByUsername(username);
        return new UserResponse.AvailableDTO(available);
    }
}
