package ru.vudovenko.micro.planner.users.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.entity.User;
import ru.vudovenko.micro.planner.plannerutils.pageRequestCreator.PageRequestCreator;
import ru.vudovenko.micro.planner.users.dto.UserDTO;
import ru.vudovenko.micro.planner.users.keycloak.KeycloakUtils;
import ru.vudovenko.micro.planner.users.searchValues.UserSearchValuesDTO;
import ru.vudovenko.micro.planner.users.service.UserService;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminController {

    private final UserService userService;
    private final KeycloakUtils keycloakUtils;

    private static final int CONFLICT = 409;
    private static final String USER_ROLE_NAME = "user";
    private static final String ADMIN_ROLE_NAME = "admin";

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody UserDTO user) {
//        if (!user.id().isEmpty()) {
//            return new ResponseEntity<>("redundant param MUST be null",
//                    HttpStatus.NOT_ACCEPTABLE);
//        }

        if (user.email() == null || user.email().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: email MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.username() == null || user.username().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: username MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.password() == null || user.password().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: password MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        Response response = keycloakUtils.createKeycloakUser(user);

        if (response.getStatus() == CONFLICT) {
            return new ResponseEntity<>("User or email " + user.email() + " already exists",
                    HttpStatus.CONFLICT);
        }

        // получаем его ID
        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info("Created user with id: {}", userId);

        List<String> defaultRoles = new ArrayList<>();
        // роли должны присутствовать в Keycloak на уровне Realm
        defaultRoles.add(USER_ROLE_NAME);
        defaultRoles.add(ADMIN_ROLE_NAME);

        KeycloakUtils.addRoles(userId, defaultRoles);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody User user) {
        if (user.getId() == null || user.getId() == 0) {
            return new ResponseEntity<>("missed param: id MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: email MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: username MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: password MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            userService.update(user);
        } catch (NoSuchElementException e) {
            return new ResponseEntity("User with id: " + user.getId() + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/delete-by-id")
    public ResponseEntity<?> deleteByUserId(@RequestBody Long id) {
        try {
            userService.deleteByUserId(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("User with id: " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/delete-by-email")
    public ResponseEntity<?> deleteByUserEmail(@RequestBody String email) {
        try {
            userService.deleteByUserEmail(email);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("User with email: " + email + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }

        return new ResponseEntity<>("User with id: " + id + " not found",
                HttpStatus.NO_CONTENT);
    }

    @PostMapping("/email")
    public ResponseEntity<?> findByEmail(@RequestBody String email) {
        User user;
        try {
            user = userService.findByEmail(email);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("User with email: " + email + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<User>> search(@RequestBody UserSearchValuesDTO userSearchValuesDTO) {
        PageRequest pageRequest = PageRequestCreator.createPageRequest(userSearchValuesDTO.pageNumber(),
                userSearchValuesDTO.pageSize(),
                userSearchValuesDTO.sortDirection(),
                userSearchValuesDTO.sortColumn());

        // результат запроса с постраничным выводом
        Page<User> result = userService.findByParams(userSearchValuesDTO.email(),
                userSearchValuesDTO.username(),
                pageRequest);

        // результат запроса
        return ResponseEntity.ok(result);
    }
}
