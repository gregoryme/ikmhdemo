package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    public Faculty findByUser(User user);

    public List<Faculty> findAllByActiveIsTrue();


}
