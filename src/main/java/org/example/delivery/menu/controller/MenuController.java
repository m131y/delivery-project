package org.example.delivery.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.delivery.menu.dto.MenuCreateRequest;
import org.example.delivery.menu.dto.MenuResponse;
import org.example.delivery.menu.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@AuthenticationPrincipal String username, @RequestBody @Valid MenuCreateRequest request) {
        return ResponseEntity.ok(menuService.createMenu(username, request));
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId, @AuthenticationPrincipal String username, @RequestBody @Valid MenuCreateRequest request) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, username, request));
    }

    @DeleteMapping("/{menuId}")
    public void deleteMenu(@PathVariable Long menuId, @AuthenticationPrincipal String username) {
        menuService.deleteMenu(menuId, username);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus() {
        return ResponseEntity.ok(menuService.getMenus());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> getMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenu(menuId));
    }
}
