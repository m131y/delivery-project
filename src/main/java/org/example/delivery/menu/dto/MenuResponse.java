package org.example.delivery.menu.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuResponse {
    private Long id;
    private Long price;
    private String menuName;
    private boolean isDeleted;
}
