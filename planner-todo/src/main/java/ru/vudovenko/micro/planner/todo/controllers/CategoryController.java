package ru.vudovenko.micro.planner.todo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vudovenko.micro.planner.entity.Category;
import ru.vudovenko.micro.planner.plannerutils.exchangeRequests.interfaces.RequestExchanger;
import ru.vudovenko.micro.planner.todo.search.CategorySearchValuesDTO;
import ru.vudovenko.micro.planner.todo.service.CategoryService;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
 * иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON
 * <p>
 * Названия методов могут быть любыми, главное не дублировать их имена внутри класса и URL mapping
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    @Qualifier("userWebClient")
    private final RequestExchanger requestExchanger;

    @PostMapping("/all")
    public List<Category> findAll(@RequestBody Long userId) {
        return categoryService.findAll(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<Category> add(@RequestBody Category category) {

        // проверка на обязательные параметры
        if (category.getId() != null && category.getId() != 0) { // это означает, что id заполнено
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: id MUST be null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (requestExchanger.isUserExisting(category.getUserId())) {
            return ResponseEntity.ok(categoryService.add(category));
        }

        return new ResponseEntity("user with id: " + category.getUserId() + " not found",
                HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Category category) {
        if (category.getId() == null || category.getId() == 0) {
            return new ResponseEntity("missed param: id MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity("missed param: title MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            categoryService.findById(category.getId());
        } catch (NoSuchElementException e) {
            return new ResponseEntity("category with id: " + category.getId() + " not found",
                    HttpStatus.NOT_FOUND);
        }

        categoryService.update(category);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            Category category = categoryService.findById(id);
            categoryService.deleteById(category.getId());
        } catch (NoSuchElementException e) {
            return new ResponseEntity("Category with id " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValuesDTO categorySearchValuesDTO) {
        if (categorySearchValuesDTO.getUserId() == null || categorySearchValuesDTO.getUserId() == 0) {
            return new ResponseEntity("missed param: userId MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        List<Category> categories = categoryService
                .findByTitle(categorySearchValuesDTO.getTitle(), categorySearchValuesDTO.getUserId());

        return ResponseEntity.ok(categories);
    }

    @PostMapping("/id")
    public ResponseEntity<Category> findById(@RequestBody Long id) {
        Category category;
        try {
            category = categoryService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity("Category with id " + id + " not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(category);
    }
}
