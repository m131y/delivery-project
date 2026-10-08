package org.example.delivery.user.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.delivery.user.entity.Role;
import org.example.delivery.user.entity.User;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private Role role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
