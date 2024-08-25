package ru.vudovenko.micro.planner.users.searchValues;

/**
 * DTO с возможными значениями, по которым можно искать пользователи + значения сортировки
 * <p>
 * <i>такие же названия должны быть у объекта на frontend</i>
 */
public record UserSearchValuesDTO(

        String email,
        String username,

        Integer pageNumber,
        Integer pageSize,

        String sortColumn,
        String sortDirection
) {
}
