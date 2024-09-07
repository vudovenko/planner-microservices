package ru.vudovenko.micro.planner.todo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vudovenko.micro.planner.entity.Priority;
import ru.vudovenko.micro.planner.todo.repo.PriorityRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PriorityService {

    private final PriorityRepository repository;

    public List<Priority> findAll(String userId) {
        return repository.findByUserIdOrderByIdAsc(userId);
    }

    public Priority add(Priority priority) {
        return repository.save(priority);
    }

    public Priority update(Priority priority) {
        return repository.save(priority);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Priority findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Priority> findByTitle(String title, String userId) {
        return repository.findByTitle(title, userId);
    }
}
