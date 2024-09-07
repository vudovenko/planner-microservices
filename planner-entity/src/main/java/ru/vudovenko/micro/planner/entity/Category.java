package ru.vudovenko.micro.planner.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Objects;


/**
 * справочноное значение - категория пользователя
 * может использовать для своих задач
 * содержит статистику по каждой категории
 */
@Entity
@Table(name = "category", schema = "todo", catalog = "planner_todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    private String title;

    // Это поле высчитывается автоматически в триггерах
    @Column(name = "completed_count", updatable = false)
    private Long completedCount;

    // Это поле высчитывается автоматически в триггерах
    @Column(name = "uncompleted_count", updatable = false)
    private Long uncompletedCount;

    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
