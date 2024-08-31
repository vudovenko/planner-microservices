package ru.vudovenko.micro.planner.users.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.entity.User;
import ru.vudovenko.micro.planner.plannerutils.pageRequestCreator.PageRequestCreator;
import ru.vudovenko.micro.planner.users.mq.func.MessageFuncActions;
import ru.vudovenko.micro.planner.users.searchValues.UserSearchValuesDTO;
import ru.vudovenko.micro.planner.users.service.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final MessageFuncActions messageFuncActions;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody User user) {
        if (user.getId() != null && user.getId() != 0) {
            return new ResponseEntity<>("redundant param MUST be null",
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

        user = userService.add(user);

        if (user != null) {
            messageFuncActions.sendNewUserMessage(user.getId());
        }

        return ResponseEntity.ok(user);
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
