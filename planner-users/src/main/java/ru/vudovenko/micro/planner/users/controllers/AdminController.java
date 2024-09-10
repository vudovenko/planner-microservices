package ru.vudovenko.micro.planner.users.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.users.dto.KeycloakUserDto;
import ru.vudovenko.micro.planner.users.dto.UserDTO;
import ru.vudovenko.micro.planner.users.keycloak.KeycloakUtils;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminController {

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

        if (user.firstname() == null || user.firstname().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: firstname MUST be not null",
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
    public ResponseEntity<?> update(@RequestBody UserDTO userDTO) {
        if (userDTO.id().isBlank() || KeycloakUtils.getUserById(userDTO.id()).isEmpty()) {
            return new ResponseEntity<>("missed param: id MUST be not null" +
                    " or user with id: " + userDTO.id() + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (userDTO.email() == null || userDTO.email().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: email MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (userDTO.firstname() == null || userDTO.firstname().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: firstname MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (userDTO.password() == null || userDTO.password().trim().isEmpty()) {
            return new ResponseEntity<>("missed param: password MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        keycloakUtils.updateKeycloakUser(userDTO);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/delete-by-id")
    public ResponseEntity<?> deleteByUserId(@RequestBody String userId) {
        Boolean deleted = KeycloakUtils.deleteKeycloakUserById(userId);

        return ResponseEntity.ok(deleted);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody String id) {
        Optional<UserRepresentation> userById = KeycloakUtils.getUserById(id);

        if (userById.isEmpty()) {
            return new ResponseEntity<>("User with id: " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(userById.get());
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserRepresentation>> search(@RequestBody KeycloakUserDto keycloakUserDto) {
        return ResponseEntity.ok(KeycloakUtils.searchKeycloakUsers(keycloakUserDto));
    }
}
