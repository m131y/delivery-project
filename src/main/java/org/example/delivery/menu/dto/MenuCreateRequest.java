package org.example.delivery.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuCreateRequest {
    @NotNull(message = "가격은 필수 입력입니다.")
    @Positive(message = "가격은 0원보다 커야 합니다.")
    private Long price;
    @NotBlank(message = "메뉴 이름은 필수 입력입니다.")
    private String menuName;
}
