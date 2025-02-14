package ru.practicum.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.admin.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}