package org.example.delivery.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.delivery.user.entity.Role;

@Data
public class SignupRequest {

    @NotBlank(message = "로그인 ID는 필수 입력입니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "패스워드는 필수 입력입니다.")
    @Size(min = 8, message = "패스워드는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력입니다.")
    private String nickname;

    @NotNull(message = "계정유형은 필수 선택입니다.")
    private Role role;
}
