package ru.vudovenko.micro.planner.users.mq.func;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;

/**
 * Класс для описания всех каналов с помощью функциональных методов.
 * <p>
 * <i>Спринг считывает объявленные бины и создает соответствующие объекты, связанные с каналами Spring Cloud Stream</i>
 */
@Getter
@Configuration
public class MessageFunc {

    /**
     * Внутрення шина, из которой будут отправляться сообщения в канал SCS по требованию.
     * <p>
     * {@code Sinks} - специальный объект из Reactive Streams,
     * который программно сигнализирует, что появилось новое сообщение.
     * <p>
     * {@code Many} - метод, который создает канал для многократной отправки сообщений.
     * <p>
     * Канал настроен на многократную отправку сообщений {@code multicast} и уведомляет всех подписчиков.
     * <p>
     * Задан начальный размер буфера {@code Queues.SMALL_BUFFER_SIZE}.
     * <p>
     * Если все подписчики отпишутся, то труба не закроется, т.к. autoCancel - {@code false}.
     */
    private final Sinks.Many<Message<Long>> innerBus
            = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    /**
     * Отправляет в канал id пользователя для создания тестовых данных.
     * Как только во внутреннюю шину {@code innerBus} будет добавлено сообщение,
     * будет сразу же отправлено сообщение в соответствующий канал SCS.
     * <p>
     * Возвращает поток данных {@code Flux<Message<Long>>},
     * представляющий собой последовательность сообщений типа {@code Message},
     * содержащих значения типа {@code Long}.
     * <p>
     * Это можно рассматривать как очередь сообщений,
     * в которой сообщения добавляются и извлекаются в порядке их поступления.
     * <p>
     * Подписчики могут выполнять различные действия с данными,
     * такие как фильтрация, преобразование, объединение и т. д.
     *
     * @return поток данных для обработки подписчиками
     */
    @Bean
    public Supplier<Flux<Message<Long>>> newUserActionProduce() {
        return innerBus::asFlux; // будет считывать данные из потока Flux (как только туда попадают новые сообщения)
    }
}
