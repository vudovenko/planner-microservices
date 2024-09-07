package ru.vudovenko.micro.planner.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.vudovenko.micro.planner.entity.Category;
import ru.vudovenko.micro.planner.entity.Priority;
import ru.vudovenko.micro.planner.entity.Task;

import java.util.Calendar;
import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class TestDataService {

    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    @KafkaListener(topics = "vudovenko-topic")
    public void listenKafka(String userId) {
        log.info("new userId: {}", userId);
        initTestData(userId);
    }

    public void initTestData(String userId) {
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
    }

    private Priority createPriority(String title, String color, String userId) {
        Priority priority = new Priority();
        priority.setTitle(title);
        priority.setColor(color);
        priority.setUserId(userId);
        return priority;
    }

    private Category createCategory(String title, String userId) {
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
                            Boolean completed, Date taskDate, String userId) {
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
