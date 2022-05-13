package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.config.Constant;
import dev.kofe.ikmhdemo.model.*;
import dev.kofe.ikmhdemo.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Application matter controller
 */

@Controller
public class ApplicationController {

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolStudioService schoolStudioService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    private Logger logger = LogManager.getLogger(ApplicationController.class);

    /**
     * Show a main page for applicant
     */

    @GetMapping("/application")
    public String applicationEditor(Model model) {
        User user = userService.getCurrentUser();
        Application application;

        if (user.getAcademyMember() != AcademyMember.STUDENT) {

            return "index";

        } else {

            application = applicationService.getByUser(user);

            if (application == null) {

                // create new application
                application = new Application();
                application.setUser(user);

                // for the sort matter if field is empty
                application.setStudentSurname(" ");

                // school studio matter
                SchoolStudio schoolStudio = schoolStudioService.getSchoolById(1L);
                application.setSchoolStudio(schoolStudio);

                model.addAttribute("newApplication", "true");

            } else {
                model.addAttribute("newApplication", "false");
            }

            application.setStudentEmail(user.getUsername());

            // try to get a school
            SchoolStudio school = application.getSchoolStudio();
            if (school != null) {
                application.setSchoolId(application.getSchoolStudio().getId());
            }

            model.addAttribute("application", application);

            Deadline deadline = deadlineService.getDeadlineById(1L);

            if (deadline == null) {
                logger.error("applicationEditor: Deadline with ID = 1L is not found (probably it is not initialized)");
                throw new RuntimeException();
            }

            model.addAttribute("deadline", deadline);

            // load school studios:
            List<SchoolStudio> schools = schoolStudioService.getAllSchools();
            model.addAttribute("schools", schools);
        }

        return "application_editor";
    }

    /**
     * Two-pass URL checker
     */

    private boolean isLinkURLisOK(String linkURL) {
        final int TIMEOUT = 3000;
        boolean check_first_pass = false;
        boolean check_second_pass = false;

        // first pass
        Document page = null;
        try {
            page = Jsoup.parse(new URL(linkURL), TIMEOUT);
            check_first_pass = true;
        } catch (IOException e) {
        }

        // second pass
        try {
            URL checkURL = new URL(linkURL);
            check_second_pass = true;
        } catch (MalformedURLException e) {

        }

        return check_first_pass && check_second_pass;
    }

    /**
     * Save the application
     */

    @PostMapping("/saveTheApplication")
    public String saveTheApplication(
            @ModelAttribute("application") Application application,
            @RequestParam("deadlineMissed") boolean deadlineMissed,
            Model model) {

        // save in any case
        SchoolStudio schoolStudio = schoolStudioService.getSchoolById(application.getSchoolId());
        if (schoolStudio == null)  {
            logger.error("saveTheApplication: school studio with ID " + application.getSchoolId() + " not found.");
            throw new RuntimeException();
        }

        application.setSchoolStudio(schoolStudio);
        applicationService.saveApplication(application);

        // check URL links and form a string with the message about the trouble with link

        boolean isLinkGoodArray[] =
                        { isLinkURLisOK(application.getWorkExampleLink1()),
                          isLinkURLisOK(application.getWorkExampleLink2()),
                          isLinkURLisOK(application.getWorkExampleLink3()) };

        boolean flag = false; // will use this flag to understand: are there any "trouble" web-link on works

        String linkCheckingString = "Your link(s) to your own work:"; // init string. String will be used if flag = true

        for (int isLinkGoodArrayIndex = 0; isLinkGoodArrayIndex < isLinkGoodArray.length; isLinkGoodArrayIndex++) {
            if (!isLinkGoodArray[isLinkGoodArrayIndex]) {
                linkCheckingString += " " + "N" + (isLinkGoodArrayIndex + 1);
                flag = true;
            }
        }

        // There are links (link) with trouble
        if (flag) {
            linkCheckingString += " is (are) not correct.";
            model.addAttribute("linkCheckingString", linkCheckingString);
            model.addAttribute("is_link_URL_valid", false);
            return "application_response";
        } else {
            model.addAttribute("is_link_URL_valid", true);
        }

        // check an agreement
        if (!application.isAgreementPrivateDataProcessing()) {

            model.addAttribute("agreement", false);
            model.addAttribute("noAgreementText",
                    "Sorry, we can not processing your application because you have not checked an agreement.");

            return "application_response";
        }

        // it is OK with the agreement
        model.addAttribute("agreement", true);

        // email matter
        User user = userService.getCurrentUser();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUsername());
        message.setFrom(Constant.EMAIL_SENDER_USERNAME);
        message.setSubject("Your application");
        String messageText =
                "Hello!\n\n" +
                        "You just have been applied to the Summer Program of the Academy.\n\n" +
                        "Your application:\n\n" +
                        "Name: " + application.getStudentName() + "\n" +
                        "Middle name: " + application.getStudentMiddleName() + "\n" +
                        "Surname: " + application.getStudentSurname() + "\n" +
                        "Email: " + application.getStudentEmail() + "\n" +
                        "School Studio Name: " +
                        schoolStudioService.getSchoolById(application.getSchoolId()).getSchoolName() + "\n" +
                        "School Studio City: " +
                        schoolStudioService.getSchoolById(application.getSchoolId()).getSchoolCity() + "\n\n" +

                        "ATTENTION. Please, do not answer on this email. Feel free to contact us by email: " +
                        Constant.ADMIN_USER_NAME + "! Thank you!" +

                        "\n\nBest regards,\n\nAcademy.";

        message.setText(messageText);
        emailSender.send(message);

        model.addAttribute("applicationString", messageText);

        return "application_response";
    }

    /**
     * Delete the application
     */

    @GetMapping("/deleteTheApplication/{id}")
    public String deleteTheApplication(@PathVariable(value = "id") Long idApplication) {

        // get the application in general
        Application application = applicationService.getById(idApplication);
        if (application == null) {
            logger.error("deleteTheApplication: application with ID " + idApplication + " not found.");
            throw new RuntimeException();
        }

        // delete ALL votes (if present) linked with the application
        voteService.deleteByApplication(application);

        // delete application
        if (!applicationService.deleteApplicationById(application.getId())) {
            logger.error("deleteTheApplication, ID=" + idApplication + ": the delete application operation went wrong.");
            throw new RuntimeException();
        }

        // delete ALL tokens (if present) linked with the application
        confirmationTokenService.deleteTokensByUser(application.getUser());

        // delete user
        if (!userService.deleteUserById(application.getUser().getId())) {
            logger.error("deleteTheApplication, ID=" + idApplication + ": the delete user operation went wrong.");
            throw new RuntimeException();
        }

        return "redirect:/admin";
    }

}
