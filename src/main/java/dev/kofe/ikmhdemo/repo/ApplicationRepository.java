package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.Application;
import dev.kofe.ikmhdemo.model.SchoolStudio;
import dev.kofe.ikmhdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Application findByUser(User user);

    List<Application> findBySchoolStudio(SchoolStudio schoolStudio);

    List<Application> findAllByDeadlineMissedIsFalse();

    List<Application> findBySchoolStudioAndDeadlineMissedIsFalse(SchoolStudio schoolStudio);

}
