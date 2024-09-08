package ru.vudovenko.micro.planner.users.dto;

public record KeycloakUserDto(
        String searchValue,
        Integer pageNumber,
        Integer pageSize
) {
}
