package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameIgnoreCase(String username);
}
