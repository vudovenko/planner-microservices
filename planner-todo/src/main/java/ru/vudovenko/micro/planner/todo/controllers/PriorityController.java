package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.entity.Priority;
import ru.vudovenko.micro.planner.todo.search.PrioritySearchValuesDTO;
import ru.vudovenko.micro.planner.todo.service.PriorityService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/priority")
public class PriorityController {

    private final PriorityService priorityService;

    @PostMapping("/all")
    public List<Priority> findAll(@RequestBody String userId) {
        return priorityService.findAll(userId);
    }


    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority,
                                        @AuthenticationPrincipal Jwt jwt) {
        // UUID пользователя из KeyCloak
        priority.setUserId(jwt.getSubject());

        // проверка на обязательные параметры
        if (priority.getId() != null && priority.getId() != 0) {
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: priority id MUST be null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение color
        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity("missed param: color",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getUserId().isBlank()) {
            return new ResponseEntity("missed param: userId MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priorityService.add(priority));
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Priority priority) {
        // проверка на обязательные параметры
        if (priority.getId() == null || priority.getId() == 0) {
            return new ResponseEntity("missed param: id",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение color
        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity("missed param: color",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            priorityService.findById(priority.getId());
        } catch (NoSuchElementException e) {
            return new ResponseEntity("priority with id: " + priority.getId() + " not found",
                    HttpStatus.NOT_FOUND);
        }

        priorityService.update(priority);

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }

    @PostMapping("/id")
    public ResponseEntity<Priority> findById(@RequestBody Long id) {
        Priority priority;
        try {
            priority = priorityService.findById(id);
        } catch (NoSuchElementException e) { // если объект не будет найден
            return new ResponseEntity("Priority with id " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priority);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        try {
            Priority priority = priorityService.findById(id);
            priorityService.deleteById(priority.getId());
        } catch (NoSuchElementException e) {
            return new ResponseEntity("Priority with id " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }

    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValuesDTO prioritySearchValuesDto,
                                                 @AuthenticationPrincipal Jwt jwt) {
        // UUID пользователя из KeyCloak
        prioritySearchValuesDto.setUserId(jwt.getSubject());

        // проверка на обязательные параметры
        if (prioritySearchValuesDto.getUserId().isBlank()) {
            return new ResponseEntity("missed param: user id MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        List<Priority> priorities = priorityService
                .findByTitle(prioritySearchValuesDto.getTitle(), prioritySearchValuesDto.getUserId());

        return ResponseEntity.ok(priorities);
    }
}
