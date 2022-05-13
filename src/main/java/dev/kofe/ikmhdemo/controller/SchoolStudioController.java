package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.model.Application;
import dev.kofe.ikmhdemo.model.SchoolStudio;
import dev.kofe.ikmhdemo.service.ApplicationService;
import dev.kofe.ikmhdemo.service.SchoolStudioService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

/**
 * School Studio controller
 */

@Controller
public class SchoolStudioController {

    @Autowired
    private SchoolStudioService schoolStudioService;

    @Autowired
    private ApplicationService applicationService;

    private Logger logger = LogManager.getLogger(SchoolStudioController.class);

    /**
     * Show a page with list of presented school studios and with the form to create new one
     */

    @GetMapping("/showSchoolsPage")
    public String showAllSchoolsAndFormNewSchool(Model model) {
        SchoolStudio newSchoolStudio = new SchoolStudio();
        model.addAttribute("newSchool", newSchoolStudio);
        model.addAttribute("schoolsList", schoolStudioService.getAllSchools());
        return "admin_school_studios";
    }

    /**
     * Save new school
     */

    @PostMapping("/saveNewSchool")
    public String saveNewSchoolStudio(@ModelAttribute("newSchool") SchoolStudio schoolStudio, Model model) {
        schoolStudioService.saveNewSchool(schoolStudio);
        SchoolStudio newSchoolStudio = new SchoolStudio();
        model.addAttribute("newSchool", newSchoolStudio);
        model.addAttribute("schoolsList", schoolStudioService.getAllSchools());
        return "admin_school_studios"; //@ToDo redirect
    }

    /**
     * Show the page with form to edit the school
     */

    @GetMapping("/editTheSchool/{id}")
    public String editTheSchool(@PathVariable(value = "id") long id, Model model) {

        SchoolStudio school = schoolStudioService.getSchoolById(id);
        if (school == null) {
            logger.error("editTheSchool: school studio with ID: " + id + " not found");
            throw new RuntimeException();
        }

        model.addAttribute("school", school);
        return "admin_school_editor";
    }

    /**
     * Save the school
     */

    @PostMapping("/saveTheSchool")
    public String saveTheSchool (@ModelAttribute("school") SchoolStudio school, Model model) {
        schoolStudioService.saveTheStudio(school);
        model.addAttribute("school", school);
        return "admin_school_editor";
    }

    /**
     * Delete the school
     */

    @GetMapping("/deleteTheSchool/{id}")
    public String deleteTheSchool (@PathVariable(value = "id") Long idSchoolStudio,
                                   Model model) {

        SchoolStudio school = schoolStudioService.getSchoolById(idSchoolStudio);
        if (school == null) {
            logger.error("deleteTheSchool: school studio with ID: " + idSchoolStudio + " not found");
            throw new RuntimeException();
        }

        // replace on the "zero" ("another") school studio
        List<Application> applicationList = applicationService.getApplicationsBySchool(school);
        for (Application application : applicationList) {
            SchoolStudio zeroSchool = schoolStudioService.getSchoolById(1L);
            if (zeroSchool == null) {
                logger.error("deleteTheSchool: school with ID = 1L not found. Probably it is not initialized");
                throw new RuntimeException();
            }
            application.setSchoolStudio(zeroSchool);
        }

        // delete
        schoolStudioService.deleteSchoolStudio(school);

        return "redirect:/showSchoolsPage";
    }

}
