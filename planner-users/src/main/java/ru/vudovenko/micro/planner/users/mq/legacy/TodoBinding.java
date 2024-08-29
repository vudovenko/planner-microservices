package ru.vudovenko.micro.planner.users.mq.legacy;

/**
 * Интерфейс, который нужен для работы MQ. Тут создаются нужные каналы.
 * <p>
 * Spring Cloud Stream автоматически создаст канал с указанным именем
 * и предоставит его для использования в приложении
 */
public interface TodoBinding {
//
//    /**
//     * нужен, чтобы на него ссылаться, а не везде использовать антипаттерн magic string
//     */
//    String OUTPUT_CHANNEL = "todoOutputChannel";
//
//    /**
//     * Создает канал для отправки сообщений.
//     * <p>
//     * <i>*Название канала может браться из названия метода (если не указано в аннотации)*</i>
//     *
//     * @return канал для отправки сообщений
//     */
//    @Output(OUTPUT_CHANNEL)
//    MessageChannel todoOutputChannel();
}
