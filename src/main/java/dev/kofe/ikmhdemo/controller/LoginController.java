package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.model.AcademyMember;
import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.Role;
import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.service.FacultyService;
import dev.kofe.ikmhdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Login controller
 */

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private FacultyService facultyService;

    /**
     * The point of enter
     */

    @GetMapping("/")
    public String viewHomePage() {

        User currentUser = userService.getCurrentUser();

        String returnString = "index";

        if (currentUser.getUsername() == null) return "login";

        // who is this?
        if (currentUser.getAcademyMember() == AcademyMember.FACULTY) {
            Faculty faculty = facultyService.getByUser(currentUser);
            if (faculty == null) {
                // new faculty member
                returnString = "redirect:/faculty_editor";
            } else {
                // there is not a new faculty member
                returnString = "redirect:/faculty";
            }
        }

        if (currentUser.getAcademyMember() == AcademyMember.STUDENT) {
            returnString = "redirect:/application";
        }

        for (Role role : currentUser.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                returnString = "redirect:/admin";
            }
        }

        return returnString;
    }

    /**
     * Show the Contact page
     */

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

    /**
     * Show the Help page
     */

    @GetMapping("/help")
    public String showHelpPage() {
        return "help";
    }

}
