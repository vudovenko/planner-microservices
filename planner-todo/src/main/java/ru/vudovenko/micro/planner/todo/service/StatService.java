package ru.vudovenko.micro.planner.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vudovenko.micro.planner.entity.Stat;
import ru.vudovenko.micro.planner.todo.repo.StatRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class StatService {

    private final StatRepository repository;

    public Stat findStatByUserId(Long userId) {
        return repository.findByUserId(userId).orElseThrow();
    }
}
