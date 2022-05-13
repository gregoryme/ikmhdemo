package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
