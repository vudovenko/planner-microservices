package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vudovenko.micro.planner.todo.service.TestDataService;

/**
 * Контроллер для создания тестовых данных
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestBody String userId) {
        testDataService.initTestData(userId);

        // если пользователя НЕ существует
        return ResponseEntity.ok(true);
    }
}
