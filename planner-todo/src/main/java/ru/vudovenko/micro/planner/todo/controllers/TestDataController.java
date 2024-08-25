package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vudovenko.micro.planner.entity.Category;
import ru.vudovenko.micro.planner.entity.Priority;
import ru.vudovenko.micro.planner.entity.Task;
import ru.vudovenko.micro.planner.todo.service.CategoryService;
import ru.vudovenko.micro.planner.todo.service.PriorityService;
import ru.vudovenko.micro.planner.todo.service.TaskService;

import java.util.Calendar;
import java.util.Date;

/**
 * Контроллер для создания тестовых данных
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
public class TestDataController {

    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestBody Long userId) {
        Priority prior1 = createPriority("Важный", "#fff", userId);
        Priority prior2 = createPriority("Неважный", "#ffе", userId);

        priorityService.add(prior1);
        priorityService.add(prior2);

        Category cat1 = createCategory("Работа", userId);
        Category cat2 = createCategory("Семья", userId);

        categoryService.add(cat1);
        categoryService.add(cat2);

        Date tomorrow = getDateInFuture(1);
        Date oneWeek = getDateInFuture(7);

        Task task1 = createTask("Покушать", cat1, prior1, true, tomorrow, userId);
        Task task2 = createTask("Поспать", cat2, prior2, false, oneWeek, userId);

        taskService.add(task1);
        taskService.add(task2);

        // если пользователя НЕ существует
        return ResponseEntity.ok(true);
    }

    private Priority createPriority(String title, String color, Long userId) {
        Priority priority = new Priority();
        priority.setTitle(title);
        priority.setColor(color);
        priority.setUserId(userId);
        return priority;
    }

    private Category createCategory(String title, Long userId) {
        Category category = new Category();
        category.setTitle(title);
        category.setUserId(userId);
        return category;
    }

    private Date getDateInFuture(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    private Task createTask(String title, Category category, Priority priority,
                            Boolean completed, Date taskDate, Long userId) {
        Task task = new Task();
        task.setTitle(title);
        task.setCategory(category);
        task.setPriority(priority);
        task.setCompleted(completed);
        task.setTaskDate(taskDate);
        task.setUserId(userId);
        return task;
    }
}
