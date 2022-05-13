package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.model.AcademyMember;
import dev.kofe.ikmhdemo.model.Deadline;
import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Faculty Member matter controller
 */

@Controller
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    private Logger logger = LogManager.getLogger(FacultyController.class);

    /**
     * Faculty member account editor
     */

    @GetMapping("/faculty_editor")
    public String showFacultyEditor(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getAcademyMember() != AcademyMember.FACULTY) return "index";

        Faculty faculty = facultyService.getByUser(currentUser);
        if (faculty == null) {
            // this is a new faculty
            faculty = new Faculty();
            faculty.setUser(currentUser);
            model.addAttribute("firstTime", true);
        } else {
            model.addAttribute("firstTime", false);
        }

        faculty.setEmail(currentUser.getUsername());
        model.addAttribute("faculty", faculty);

        return "faculty_editor";
    }

    /**
     * Show a main page for the faculty member
     */

    @GetMapping("/faculty")
    public String facultyCenter (Model model) {

        User currentUser = userService.getCurrentUser();
        if (currentUser.getAcademyMember() != AcademyMember.FACULTY) return "redirect:/";
        Faculty faculty = facultyService.getByUser(currentUser);
        if (faculty == null) {
            return "redirect:/";
        }
        model.addAttribute("title", faculty.getTitle());
        model.addAttribute("name", faculty.getName());
        model.addAttribute("surname", faculty.getSurname());
        Deadline deadline = deadlineService.getDeadlineById(1L);
        model.addAttribute("deadline", deadline);

        return "faculty";
    }

    /**
     * Save the faculty member account
     */

    @PostMapping("/saveFacultyCard")
    public String saveFacultyCard (@ModelAttribute("faculty") Faculty faculty, Model model) {
        facultyService.saveFaculty(faculty);
        model.addAttribute("saved", "Saved!");
        model.addAttribute("firstTime", false);
        model.addAttribute("faculty", faculty);
        return "faculty_editor";
    }

    /**
     * Delete the faculty member account
     */

    @GetMapping("/deleteTheFacultyMember/{id}")
    public String deleteFacultyMember(@PathVariable(value = "id") Long facultyId) {

        Faculty facultyMember = facultyService.getById(facultyId);
        if (facultyMember == null) {
            logger.error("deleteFacultyMember: the faculty with ID " + facultyId + " not found");
            throw new RuntimeException();
        }

        // delete all votes linked with the faculty member
        voteService.deleteByFacultyMember(facultyMember);

        // delete all tokens linked with the faculty member
        confirmationTokenService.deleteTokensByUser(facultyMember.getUser());

        // delete the faculty member account
        if (!facultyService.deleteById(facultyMember.getId())) {
            logger.error("deleteFacultyMember: faculty member account with ID " + facultyMember.getId() + " not deleted");
            throw new RuntimeException();
        }

        // delete user linked with the faculty member
        if (!userService.deleteUserById(facultyMember.getUser().getId())) {
            logger.error("deleteFacultyMember: user with ID " + facultyMember.getUser().getId() + " not deleted");
            throw new RuntimeException();
        }

        return "redirect:/admin";
    }

}
