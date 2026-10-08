package org.example.delivery.menu.dto;

import lombok.Builder;
import lombok.Data;
import org.example.delivery.menu.entity.Menu;

@Data
@Builder
public class MenuResponse {
    private Long id;
    private Long price;
    private String menuName;
    private boolean isDeleted;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .menuName(menu.getMenuName())
                .price(menu.getPrice())
                .isDeleted(menu.isDeleted())
                .build();
    }
}
