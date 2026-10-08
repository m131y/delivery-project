package org.example.delivery.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.delivery.global.security.JwtUtil;
import org.example.delivery.user.dto.request.LoginRequest;
import org.example.delivery.user.dto.request.SignupRequest;
import org.example.delivery.user.dto.response.UserResponse;
import org.example.delivery.user.entity.User;
import org.example.delivery.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResponse signup(SignupRequest signupRequest) {

        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("중복된 아이디 입니다.");
        }

        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new RuntimeException("중복된 닉네임 입니다.");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .role(signupRequest.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .nickname(savedUser.getNickname())
                .role(savedUser.getRole())
                .username(savedUser.getUsername())
                .build();
    }

    public UserResponse login(LoginRequest loginRequest, HttpServletResponse res) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 ID와 비밀번호가 유효하지 않습니다."));

        // 아이디/비밀번호 중 무엇이 틀렸는지 노출하지 않도록 같은 메시지를 사용한다.
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("로그인 ID와 비밀번호가 유효하지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }
}
