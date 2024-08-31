package ru.vudovenko.micro.planner.todo.mq.func;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.vudovenko.micro.planner.todo.service.TestDataService;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class MessageFunc {

    private final TestDataService testDataService;

    // получает id пользователя и запускает создание тестовых данных
    // название метода должно совпадать с настройками definition и bindings в файлах properties (или yml)
    @Bean
    public Consumer<Message<Long>> newUserActionConsume() {
        return message -> testDataService.initTestData(message.getPayload());
    }
}
