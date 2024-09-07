package ru.vudovenko.micro.planner.plannerutils.oauth2.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования ролей из JWT-токена в роли Spring Security.
 *
 * <p>
 * Данный класс извлекает раздел "realm_access" из данных JWT, получаемого от сервера аутентификации (Keycloak),
 * и преобразует список ролей в коллекцию объектов {@link GrantedAuthority}, которые используются Spring Security для
 * проверки прав доступа.
 * </p>
 *
 * <p>
 * Например, если в JWT есть роль "admin", то данная роль будет преобразована в "ROLE_admin".
 * </p>
 *
 * Пример структуры JWT:
 * <pre>
 * "realm_access": {
 *     "roles": [
 *       "offline_access",
 *       "default-roles-todoapp-realm",
 *       "uma_authorization",
 *       "user"
 *     ]
 *   }
 * </pre>
 */
public class KCRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        // получаем доступ к разделу JSON
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        // если раздел JSON не будет найден - значит нет ролей
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList(); // пустая коллекция - нет ролей
        }

        return ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
