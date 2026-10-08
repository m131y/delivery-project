package org.example.delivery.menu.service;

import org.example.delivery.global.error.ErrorCode;
import org.example.delivery.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.example.delivery.menu.dto.MenuCreateRequest;
import org.example.delivery.menu.dto.MenuResponse;
import org.example.delivery.menu.entity.Menu;
import org.example.delivery.menu.repository.MenuRepository;
import org.example.delivery.user.entity.Role;
import org.example.delivery.user.entity.User;
import org.example.delivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public MenuResponse createMenu(String username, MenuCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!(user.getRole() == Role.OWNER)) {
            throw new BusinessException(ErrorCode.OWNER_ONLY, "사장님만 메뉴를 추가할 수 있습니다.");
        }

        Menu menu = Menu.builder()
                .menuName(request.getMenuName())
                .price(request.getPrice())
                .user(user)
                .build();

        Menu savedMenu = menuRepository.save(menu);

        return MenuResponse.from(savedMenu);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        List<Menu> menuList = menuRepository.findAllByIsDeletedFalse();
        return menuList.stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_DELETED);
        }
        return MenuResponse.from(menu);
    }

    public MenuResponse updateMenu(Long menuId, String username, MenuCreateRequest request) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!menu.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.MENU_NOT_OWNER, "본인의 메뉴만 수정할 수 있습니다.");
        }

        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_DELETED, "삭제된 메뉴는 수정할 수 없습니다.");
        }

        menu.setMenuName(request.getMenuName());
        menu.setPrice(request.getPrice());

        return MenuResponse.from(menu);
    }

    public void deleteMenu(Long menuId, String username) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!menu.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.MENU_NOT_OWNER, "본인의 메뉴만 삭제할 수 있습니다.");
        }

        menu.setDeleted(true);
    }
}
