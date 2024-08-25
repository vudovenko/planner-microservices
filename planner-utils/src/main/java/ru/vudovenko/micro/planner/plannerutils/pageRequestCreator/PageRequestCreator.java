package ru.vudovenko.micro.planner.plannerutils.pageRequestCreator;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestCreator {

    public static final String ID_COLUMN = "id";

    public static PageRequest createPageRequest(int pageNumber, int pageSize,
                                                String sortDirection, String... properties)
            throws IllegalArgumentException {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        properties = addIdToProperties(properties);

        Sort sort = Sort.by(direction, properties);

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * Последним полем для сортировки добавляем id, чтобы всегда сохранялся строгий порядок.
     * Например, если у 2-х задач одинаковое значение приоритета и мы сортируем по этому полю.
     * Порядок следования этих 2-х записей после выполнения запроса может каждый раз меняться, т.к. не указано второе поле сортировки.
     * Поэтому и используем ID - тогда все записи с одинаковым значением приоритета будут следовать в одном порядке по ID.
     *
     * @param properties поля сортировки
     * @return поля сортировки с добавленным id
     */
    private static String[] addIdToProperties(String... properties) {
        String[] result = new String[properties.length + 1];
        System.arraycopy(properties, 0, result, 0, properties.length);
        result[result.length - 1] = ID_COLUMN;
        return result;
    }
}

