package ru.vudovenko.micro.planner.todo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vudovenko.micro.planner.entity.Task;
import ru.vudovenko.micro.planner.todo.repo.TaskRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public List<Task> findAll(String userId) {
        return repository.findByUserIdOrderByTitleAsc(userId);
    }

    public Task add(Task task) {
        return repository.save(task);
    }

    public Task update(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<Task> findByParams(String text, Boolean completed, Long priorityId,
                                   Long categoryId, String userId, Date dateFrom,
                                   Date dateTo, PageRequest paging) {
        return repository.findByParams(text, completed, priorityId, categoryId,
                userId, dateFrom, dateTo, paging);
    }

    public Task findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
}
