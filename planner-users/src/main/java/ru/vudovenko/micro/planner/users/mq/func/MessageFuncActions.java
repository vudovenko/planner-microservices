package ru.vudovenko.micro.planner.users.mq.func;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

/**
 * Класс для описания всех каналов с помощью функциональных методов.
 * <p>
 * <i>Спринг считывает объявленные бины и создает соответствующие объекты, связанные с каналами Spring Cloud Stream</i>
 */
@Getter
@Service
@RequiredArgsConstructor
@Log4j2
public class MessageFuncActions {

    private final MessageFunc messageFunc;

    /**
     * Обращаемся к {@code MessageFunc} для добавления во внутреннюю шину {@code innerBus} id нового пользователя.
     * Изменения внутренней шины считываются и отправляются в канал SCS, который связан с брокером сообщений.
     * <p>
     * С помощью системного метода {@code emitNext} добавляем новое сообщение с id пользователя в шину.
     *
     * @param userId id пользователя
     */
    public void sendNewUserMessage(Long userId) {
        messageFunc.getInnerBus()
                .emitNext(MessageBuilder.withPayload(userId).build(),
                        Sinks.EmitFailureHandler.FAIL_FAST);
        log.info("Сообщение отправлено. Id = {}", userId);
    }
}
