package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.entity.Task;
import ru.vudovenko.micro.planner.plannerutils.exchangeRequests.interfaces.RequestExchanger;
import ru.vudovenko.micro.planner.plannerutils.pageRequestCreator.PageRequestCreator;
import ru.vudovenko.micro.planner.todo.search.TaskSearchValuesDTO;
import ru.vudovenko.micro.planner.todo.service.TaskService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task") // базовый URI
public class TaskController {

    private final TaskService taskService;
    @Qualifier("userWebClient")
    private final RequestExchanger requestExchanger;

    @PostMapping("/all")
    public ResponseEntity<List<Task>> findAll(@RequestBody String userId) {
        return ResponseEntity.ok(taskService.findAll(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<Task> add(@RequestBody Task task,
                                    @AuthenticationPrincipal Jwt jwt) {
        // UUID пользователя из KeyCloak
        task.setUserId(jwt.getSubject());

        // проверка на обязательные параметры
        if (task.getId() != null && task.getId() != 0) {
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: id MUST be null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (task.getUserId().isBlank()) {
            return new ResponseEntity("missed param: userId MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(taskService.add(task));
    }

    @PutMapping("/update")
    public ResponseEntity<Task> update(@RequestBody Task task) {
        if (task.getId() == null || task.getId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        taskService.update(task);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            Task task = taskService.findById(id);
            taskService.deleteById(task.getId());
        } catch (NoSuchElementException e) {
            return new ResponseEntity("id=" + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/id")
    public ResponseEntity<Task> findById(@RequestBody Long id) {
        Task task;
        try {
            task = taskService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity("id=" + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Task>> search(@RequestBody TaskSearchValuesDTO taskSearchValuesDto,
                                             @AuthenticationPrincipal Jwt jwt) {
        if (jwt.getSubject().isBlank()) {
            return new ResponseEntity("missed param: user Id",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        Boolean isCompleted = taskSearchValuesDto.completed() != null
                && taskSearchValuesDto.completed() == 1;

        // чтобы захватить в выборке все задачи по датам,
        // независимо от времени - можно выставить время с 00:00 до 23:59
        Date dateFrom = null;
        Date dateTo = null;

        // выставить 00:00 для начальной даты (если она указана)
        if (taskSearchValuesDto.dateFrom() != null) {
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(taskSearchValuesDto.dateFrom());
            calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
            calendarFrom.set(Calendar.MINUTE, 0);
            calendarFrom.set(Calendar.SECOND, 0);
            calendarFrom.set(Calendar.MILLISECOND, 0);

            dateFrom = calendarFrom.getTime(); // записываем начальную дату с 00:00
        }

        // выставить 23:59 для конечной даты (если она указана)
        if (taskSearchValuesDto.dateTo() != null) {

            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(taskSearchValuesDto.dateTo());
            calendarTo.set(Calendar.HOUR_OF_DAY, 23);
            calendarTo.set(Calendar.MINUTE, 59);
            calendarTo.set(Calendar.SECOND, 59);
            calendarTo.set(Calendar.MILLISECOND, 999);

            dateTo = calendarTo.getTime(); // записываем конечную дату с 23:59
        }

        PageRequest pageRequest = PageRequestCreator.createPageRequest(taskSearchValuesDto.pageNumber(),
                taskSearchValuesDto.pageSize(),
                taskSearchValuesDto.sortDirection(),
                taskSearchValuesDto.sortColumn());

        // результат запроса с постраничным выводом
        Page<Task> result = taskService
                .findByParams(
                        taskSearchValuesDto.title(),
                        isCompleted,
                        taskSearchValuesDto.priorityId(),
                        taskSearchValuesDto.categoryId(),
                        jwt.getSubject(),
                        dateFrom,
                        dateTo,
                        pageRequest);

        // результат запроса
        return ResponseEntity.ok(result);
    }
}
