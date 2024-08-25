package ru.vudovenko.micro.planner.plannerutils.exchangeRequests.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vudovenko.micro.planner.entity.User;
import ru.vudovenko.micro.planner.plannerutils.exchangeRequests.interfaces.RequestExchanger;

/**
 * Спец. класс для вызова микросервисов пользователей с помощью WebClient
 */
@Component("userWebClient")
public class UserWebClientBuilder implements RequestExchanger {

    @Override
    public boolean isUserExisting(Long userId) {

        try {

            User user = WebClient.create(baseUrl)
                    .post()
                    .uri("id")
                    .bodyValue(userId)
                    .retrieve()
                    .bodyToFlux(User.class)
                    .blockFirst(); // блокирует поток до получения 1й записи

            return user != null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
