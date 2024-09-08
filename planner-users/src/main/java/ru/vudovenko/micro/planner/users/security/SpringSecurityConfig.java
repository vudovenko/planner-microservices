package ru.vudovenko.micro.planner.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import ru.vudovenko.micro.planner.plannerutils.oauth2.converter.KCRoleConverter;

/**
 * Класс конфигурации для настройки безопасности приложения с использованием Spring Security.
 * Данный класс содержит конфигурацию для защиты HTTP-запросов и настроек аутентификации на основе JWT.
 *
 * <p>
 * Настройка позволяет:
 * <ul>
 *   <li>Открыть доступ к URI "/test/login" для всех пользователей (в том числе анонимных)</li>
 *   <li>Требовать аутентификацию для всех остальных запросов</li>
 *   <li>Использовать JWT-токены для аутентификации и проверять роли пользователя из JWT</li>
 * </ul>
 * </p>
 *
 * <p>
 * JWT-токены проверяются на стороне OAuth2 Resource Server, а роли пользователей извлекаются
 * с помощью кастомного конвертера {@link KCRoleConverter}, который преобразует роли, полученные
 * из Keycloak (или другого сервиса), в объекты {@link GrantedAuthority}.
 * </p>
 */
@Configuration // данный класс будет считан как конфиг для spring контейнера
@EnableWebSecurity // включает механизм защиты адресов, которые настраиваются в SecurityFilterChain
@EnableMethodSecurity() // включение механизма для защиты методов по ролям
public class SpringSecurityConfig {

    /**
     * Определяет цепочку фильтров безопасности для HTTP-запросов.
     *
     * <p>
     * Настройка включает проверку JWT-токенов, извлечение ролей пользователей из токена
     * и применение правил авторизации для определенных URI.
     * </p>
     *
     * @param http объект для настройки параметров безопасности HTTP
     * @return {@link SecurityFilterChain} для обработки запросов с учетом настроек безопасности
     * @throws Exception если возникнет ошибка при настройке безопасности
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // конвертер для настройки spring security
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // подключаем конвертер ролей
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

        // все сетевые настройки
        http
                .authorizeHttpRequests(auth -> auth // Используем новую форму записи
                        .requestMatchers("/admin/*").hasRole("admin")
                        .requestMatchers("/auth/*").hasRole("user")
                        .anyRequest().authenticated() // остальной API будет доступен только аутентифицированным пользователям
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter) // Конвертер ролей из JWT в Authority (Role)
                        )
                );

        return http.build();
    }
}
