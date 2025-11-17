package com.progressapp.progress_tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.progressapp.progress_tracker.entity.User;

// Наследуемся от JpaRepository - и получаем ВСЕ CRUD-методы бесплатно!
public interface UserRepository extends JpaRepository<User, UUID> {
    // Spring Data JPA сам реализует метод по имени
    User findByUsername(String username);
}