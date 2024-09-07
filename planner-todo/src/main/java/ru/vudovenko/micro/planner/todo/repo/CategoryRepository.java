package ru.vudovenko.micro.planner.todo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vudovenko.micro.planner.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserIdOrderByTitleAsc(String userId);

    @Query("""
            SELECT c
            FROM Category c
            WHERE lower(c.title) like lower(concat('%', :title, '%'))
                AND c.userId = :userId
            ORDER BY c.title ASC
            """)
    List<Category> findByTitle(@Param("title") String title, @Param("userId") String userId);
}
