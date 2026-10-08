package org.example.delivery.user.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.delivery.user.entity.Role;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private Role role;
}
