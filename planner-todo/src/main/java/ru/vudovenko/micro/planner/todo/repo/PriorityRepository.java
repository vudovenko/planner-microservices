package ru.vudovenko.micro.planner.todo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vudovenko.micro.planner.entity.Priority;

import java.util.List;

public interface PriorityRepository extends JpaRepository<Priority, Long> {

    @Query("""
            from Priority p
            where lower(p.title) like lower(concat('%', :title, '%'))
            and p.userId = :userId
            order by p.title asc
            """)
    List<Priority> findByTitle(@Param("title") String title, @Param("userId") String userId);

    /**
     * Поиск приоритетов пользователя по почте в порядке возрастания id
     *
     * @param userId идентификатор пользователя
     * @return список приоритетов пользователя
     */
    List<Priority> findByUserIdOrderByIdAsc(String userId);
}
