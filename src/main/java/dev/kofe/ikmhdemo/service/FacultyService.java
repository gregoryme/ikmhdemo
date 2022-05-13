package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.repo.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    public Faculty getByUser (User user) {
        return facultyRepository.findByUser(user);
    }

    public Faculty saveFaculty (Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public List<Faculty> getAllActiveFaculty() {
        return facultyRepository.findAllByActiveIsTrue();
    }

    public Faculty getById (long id) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        Faculty faculty;
        if (optionalFaculty.isPresent()) {
            faculty = optionalFaculty.get();
        } else {
            faculty = null;
        }

        return faculty;
    }


    public boolean deleteById (long id) {
        if (facultyRepository.findById(id).isPresent()) {
            facultyRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public long countAll () {
        return facultyRepository.count();
    }

}
