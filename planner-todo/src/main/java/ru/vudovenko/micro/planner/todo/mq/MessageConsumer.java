package ru.vudovenko.micro.planner.todo.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import ru.vudovenko.micro.planner.todo.service.TestDataService;

@Component
@EnableBinding(TodoBinding.class)
@RequiredArgsConstructor
public class MessageConsumer {

    private final TestDataService testDataService;

    @StreamListener(target = TodoBinding.INPUT_CHANNEL)
    public void initTestData(Long userId) {
        testDataService.initTestData(userId);
    }
}
