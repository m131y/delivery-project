package org.example.delivery.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "로그인 ID는 필수 입력입니다.")
    private String username;

    @NotBlank(message = "패스워드는 필수 입력입니다.")
    private String password;
}
