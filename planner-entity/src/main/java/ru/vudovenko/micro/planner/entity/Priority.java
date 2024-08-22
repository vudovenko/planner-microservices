package ru.vudovenko.micro.planner.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Objects;

/**
 * справочноное значение - приоритет пользователя
 * может использовать для своих задач
 */
@Entity
@Table(name = "priority", schema = "todo", catalog = "planner_todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    private String title;
    private String color;

    @Column(name = "user_id")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return id.equals(priority.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
