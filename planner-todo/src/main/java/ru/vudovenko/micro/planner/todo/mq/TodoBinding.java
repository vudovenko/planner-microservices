package ru.vudovenko.micro.planner.todo.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Интерфейс, который нужен для работы MQ. Тут создаются нужные каналы
 */
public interface TodoBinding {

    /**
     * Нужен, чтобы на него ссылаться, а не везде использовать антипаттерн magic string
     */
    String INPUT_CHANNEL = "todoInputChannel";

    /**
     * Создает канал для получения сообщений
     *
     * @return канал для получения сообщений
     */
    @Output(INPUT_CHANNEL)
    MessageChannel todoInputChannel();
}
