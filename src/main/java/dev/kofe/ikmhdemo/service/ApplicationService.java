package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.Application;
import dev.kofe.ikmhdemo.model.SchoolStudio;
import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.repo.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application getByUser (User user) {
            return applicationRepository.findByUser(user);
    }

    public Application getById(long id) {
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        Application application;
        if (optionalApplication.isPresent()) {
            application = optionalApplication.get();
        } else {
            application = null;
        }
        return application;
    }

    public long getCount() {
        return applicationRepository.count();
    }

    public Application saveApplication (Application application) {
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsBySchool (SchoolStudio schoolStudio) {
        return applicationRepository.findBySchoolStudio(schoolStudio);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> getAllWhoDidNotMissedTheDeadline() {
        return applicationRepository.findAllByDeadlineMissedIsFalse();
    }

    public List<Application> getApplicationBySchoolAndWhoDidNotMissedTheDeadline (SchoolStudio schoolStudio) {
        return applicationRepository.findBySchoolStudioAndDeadlineMissedIsFalse(schoolStudio);
    }

    public boolean deleteApplicationById(Long id) {
        if (getById(id) != null) {
            applicationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
