package org.example.delivery.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.delivery.global.error.ErrorCode;
import org.example.delivery.global.error.exception.BusinessException;
import org.example.delivery.global.security.JwtUtil;
import org.example.delivery.user.dto.request.LoginRequest;
import org.example.delivery.user.dto.request.SignupRequest;
import org.example.delivery.user.dto.response.UserResponse;
import org.example.delivery.user.entity.User;
import org.example.delivery.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResponse signup(SignupRequest signupRequest) {

        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .role(signupRequest.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest, HttpServletResponse res) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN));

        // 아이디/비밀번호 중 무엇이 틀렸는지 노출하지 않도록 같은 메시지를 사용한다.
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN);
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return UserResponse.from(user);
    }
}
