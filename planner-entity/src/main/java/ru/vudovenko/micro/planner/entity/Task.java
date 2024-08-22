package ru.vudovenko.micro.planner.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Date;
import java.util.Objects;

/**
 * задачи пользователя
 */
@Entity
@Table(name = "task", schema = "todo", catalog = "planner_todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    private String title;

    @Basic
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class) // автоматическая конвертация числа в boolean
    private Boolean completed; // 1 = true, 0 = false

    @Column(name = "task_date") // в БД поле называется task_date, т.к. нельзя использовать системное имя date
    private Date taskDate;

    // задача может иметь только один приоритет (с обратной стороны - один и тот же приоритет может быть использоваться в множестве задач)
    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "id") // по каким полям связывать (foreign key)
    private Priority priority;

    // задача может иметь только одну категорию (с обратной стороны - одна и та же категория может быть использоваться в множестве задач)
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id") // по каким полям связывать (foreign key)
    private Category category;

    @Column(name = "user_id")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
