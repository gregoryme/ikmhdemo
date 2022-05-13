package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.SchoolStudio;
import dev.kofe.ikmhdemo.repo.SchoolStudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SchoolStudioService {

    @Autowired
    private SchoolStudioRepository schoolStudioRepository;

    public List<SchoolStudio> getAllSchools() {
        return schoolStudioRepository.findAll();
    }

    public SchoolStudio saveNewSchool(SchoolStudio newSchool) {
        return schoolStudioRepository.save(newSchool);
    }

    public SchoolStudio getSchoolById(Long id) {
        Optional<SchoolStudio> optionalSchoolStudio = schoolStudioRepository.findById(id);
        SchoolStudio schoolStudio;
        if (optionalSchoolStudio.isPresent()) {
            schoolStudio = optionalSchoolStudio.get();
        } else {
            schoolStudio = null;
        }

        return schoolStudio;
    }

    public SchoolStudio saveTheStudio(SchoolStudio schoolStudio) {
        return schoolStudioRepository.save(schoolStudio);
    }

    public void deleteSchoolStudio (SchoolStudio schoolStudio) {
        schoolStudioRepository.delete(schoolStudio);
    }

    public long countAll() {
        return schoolStudioRepository.count();
    }

}
