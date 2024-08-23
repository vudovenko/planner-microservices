package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vudovenko.micro.planner.entity.Stat;
import ru.vudovenko.micro.planner.todo.service.StatService;

import java.util.NoSuchElementException;

/**
 * Чтобы дать меньше шансов для взлома (например, CSRF атак): POST/PUT запросы могут изменять/фильтровать закрытые данные, а GET запросы - для получения незащищенных данных
 * Т.е. GET-запросы не должны использоваться для изменения/получения секретных данных
 * <p>
 * Если возникнет exception - вернется код 500 Internal Server Error, поэтому не нужно все действия оборачивать в try-catch
 * <p>
 * Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
 * иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON
 * <p>
 * Названия методов могут быть любыми, главное не дублировать их имена и URL mapping
 */
@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody Long userId) {
        Stat stat;
        try {
            stat = statService.findStatByUserId(userId);
        } catch (NoSuchElementException e) { // если объект не будет найден
            return new ResponseEntity("Stat with id " + userId + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(stat);
    }
}
