package ru.vudovenko.micro.planner.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vudovenko.micro.planner.entity.User;
import ru.vudovenko.micro.planner.users.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Page<User> findByParams(String email, String username, Pageable pageable) {
        return userRepository.findByParams(email, username, pageable);
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        userRepository.findById(user.getId()).orElseThrow();
        return userRepository.save(user);
    }

    public void deleteByUserId(Long id) {
        userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUserEmail(String email) {
        userRepository.findByEmail(email).orElseThrow();
        userRepository.deleteByEmail(email);
    }
}
