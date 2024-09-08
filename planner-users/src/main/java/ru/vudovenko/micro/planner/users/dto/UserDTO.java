package ru.vudovenko.micro.planner.users.dto;


/**
 * DTO с данными пользователя
 */
public record UserDTO(
//        String id,
        String email,
        String username,
        String password
) {
}
