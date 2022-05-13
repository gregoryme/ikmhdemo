package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.Application;
import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    public List<Vote> findByFaculty(Faculty faculty);

    public Vote findByFacultyAndApplication (Faculty faculty, Application application);

    //public List<Vote> findAllByFacultyAndApplicationIsNot (Faculty faculty, Application application);

    public List<Vote> findByApplication (Application application);

    public void deleteAllByApplication(Application application);

    public void deleteAllByFaculty(Faculty faculty);

}
