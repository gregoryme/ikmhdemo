package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadlineRepository extends JpaRepository<Deadline, Long>  {

}
