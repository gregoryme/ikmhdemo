package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.config.Constant;
import dev.kofe.ikmhdemo.model.*;
import dev.kofe.ikmhdemo.service.*;
import dev.kofe.ikmhdemo.service.UrlCoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin matter controller
 */

@Controller
public class AdminController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private SchoolStudioService schoolStudioService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private UrlCoder urlCoder;

    private Logger logger = LogManager.getLogger(AdminController.class);

    /**
     *  Main point of entry to the admin panel
     */

    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        long applicationsQuantity = applicationService.getCount();
        List<Faculty> facultyList = facultyService.getAllActiveFaculty();
        model.addAttribute("applicationsQuantity", applicationsQuantity);
        model.addAttribute("activeFacultyMembersQuantity", facultyList.size());
        Deadline deadline = deadlineService.getDeadlineById(1L);

        if (deadline == null) {
            logger.error("showAdminPage: Deadline with ID = 1L is not found (probably it is not initialized)");
            throw new RuntimeException();
        }

        model.addAttribute("deadline", deadline);
        return "admin";
    }

    /**
     * Show All Faculty members
     */

    @GetMapping("/showAllFacultyMembersForAdmin")
    public String showAllFacultyMembers (Model model) {

        List<Faculty> facultyMembers = facultyService.getAllFaculty();

        // full-voted matter
        for (Faculty faculty : facultyMembers) {
            long applicationsCount = applicationService.getCount();
            if ((applicationsCount == voteService.getAllVotesByFaculty(faculty).size()) && (applicationsCount > 0)) {
                faculty.setFullVoted(true);
            } else {
                faculty.setFullVoted(false);
            }
        }

        model.addAttribute("facultyMembers", facultyMembers);

        return "admin_faculty_members";
    }

    /**
     * Show info on the faculty member with ID
     */

    @GetMapping("/infoFacultyMemberForAdmin/{id}")
    public String infoFacultyMember(@PathVariable(value = "id") long facultyId, Model model) {

        // main matter: list of faculty members

        Faculty faculty = facultyService.getById(facultyId);
        if (faculty == null) {
            logger.error("infoFacultyMember: Faculty Member with ID " + facultyId + " not found");
            throw new RuntimeException();
        }

        model.addAttribute("faculty", faculty);

        // vote matter: get information on votes of the faculty member
        List<Vote> voteListOfTheFacultyMember = voteService.getAllVotesByFaculty(faculty);

        if (voteListOfTheFacultyMember.isEmpty()) {
            model.addAttribute("areThereAnyVotes", false);
        } else
            model.addAttribute("areThereAnyVotes", true);

        model.addAttribute("votes", voteListOfTheFacultyMember);

        // applications which do not have votes by this faculty member
        List<Application> applications = applicationService.getAllApplications();
        List<Application> applicationsWithoutVotesByTheFaculty = new ArrayList<>();
        for (Application application : applications) {
            if (voteService.getVoteByFacultyAndApplication(faculty, application) == null) {
                applicationsWithoutVotesByTheFaculty.add(application);
            }
        }

        if (applicationsWithoutVotesByTheFaculty.isEmpty()) {
            model.addAttribute("areThereAnyApplicationsWithoutVoteByTheFaculty", false);
        } else {
            model.addAttribute("areThereAnyApplicationsWithoutVoteByTheFaculty", true);
        }

        model.addAttribute("applicationsWithoutVotes", applicationsWithoutVotesByTheFaculty);

        return "admin_faculty_info";
    }


    /**
     * showAllApplications -
     * one method to form a page with ALL applicants or with applicants from the concrete school
     * signature for calling: /showAllApplicantsForAdmin/{mode}/{id}/{sort}
     * mode = 0 - all applications from all schools
     * mode = 1 - applications from concrete school
     * id: id school (to display applications from concrete school)
     * order = 0 - without order
     * order = 1 - order by abc
     * order = 2 - order by votes
     * NOTE: non-missed deadline applications only
     */

    @GetMapping("/showAllApplicantsForAdmin/{mode}/{id}/{order}")
    public String showAllApplicationsNonMissedDeadline
                                      (@PathVariable(value = "mode") int mode,
                                       @PathVariable(value = "id") long id,
                                       @PathVariable(value = "order") int order,
                                       Model model) {

        model.addAttribute("modeCall", mode);
        model.addAttribute("idCall", id);
        model.addAttribute("orderCall", order);

        List<Application> applications = null;

        if (mode == 1) {
            // case: application from the concrete school
            SchoolStudio schoolStudio = schoolStudioService.getSchoolById(id);
            if (schoolStudio != null) {
                applications = applicationService.getApplicationBySchoolAndWhoDidNotMissedTheDeadline(schoolStudio);
            } else {
                logger.error("showAllApplicationsNonMissedDeadline: School Studio with ID = " + id + " not found.");
                throw new RuntimeException();
                // applications = new ArrayList<>();
            }
        }

        if (mode == 0) {
            // case: all applications from all schools
            // applications = applicationService.getAllApplications();
            applications = applicationService.getAllWhoDidNotMissedTheDeadline();
        }

        for (Application application : applications) {
            // average vote
            float currentAverageVote = 0;
            List<Vote> votesOfTheApplication = voteService.getAllVotesForTheApplication(application);
            if (votesOfTheApplication.size() == 0) {
                application.setAverageVote(0);
                application.setVotedAll(false);
                continue;
            }
            for (Vote vote : votesOfTheApplication) {
                currentAverageVote += vote.getVoteValue();
            }
            int quantityOfVotes = votesOfTheApplication.size();
            currentAverageVote = (quantityOfVotes == 0)
                    ? currentAverageVote
                    : currentAverageVote / quantityOfVotes;
            application.setAverageVote(currentAverageVote);

            // are there all votes?
            boolean allFacultyHaveBeenVoted = false;
            if (voteService.getAllVotesForTheApplication(application).size() == facultyService.getAllActiveFaculty().size()) {
                allFacultyHaveBeenVoted =true;
            }
            application.setVotedAll(allFacultyHaveBeenVoted);
        }

        List<Application> sortedApplications = null;

        // case: without sorting
        if (order == 0) {
            sortedApplications = applications;
        }

        // case: sort by surnames
        if (order == 1) {
            sortedApplications =
                    applications.stream()
                            .sorted(Comparator.comparing(Application :: getStudentSurname))
                            .collect(Collectors.toList());
        }

        // case: sort by average vote
        if (order == 2)
            sortedApplications =
                    applications.stream()
                            .sorted(Comparator.comparingDouble(Application :: getAverageVote).reversed())
                            .collect(Collectors.toList());

        model.addAttribute("isApplicationListEmpty", (sortedApplications.size() == 0) ? true : false);
        model.addAttribute("applications", sortedApplications);

        return "admin_applicants";
    }

    /**
     * Additional parameters are for navigation matter
     */

    @GetMapping("/infoApplicationForAdmin/{id}/{mode}/{id_call}/{order}")
    public String infoApplication(@PathVariable(value = "id") long applicationId,
                                  @PathVariable(value = "mode") int mode,
                                  @PathVariable(value = "id_call") long idCall,
                                  @PathVariable(value = "order") int order,
                                  Model model) {

        model.addAttribute("modeCall", mode);
        model.addAttribute("idCall", idCall);
        model.addAttribute("orderCall", order);

        Application application = applicationService.getById(applicationId);
        if (application == null) {
            logger.error("infoApplication: Application with ID " + applicationId + " not found.");
            throw new RuntimeException();
        }

        model.addAttribute("applicant", application);

        // get votes
        List<Vote> votesOfTheApplication = voteService.getAllVotesForTheApplication(application);

        if (votesOfTheApplication.size() == 0) {
            model.addAttribute("areThereAnyVotesForTheApplication", false);
        } else {
            model.addAttribute("areThereAnyVotesForTheApplication", true);
        }

        model.addAttribute("votes", votesOfTheApplication);

        // get faculty members who are active and has not voted yet for this application
        List <Faculty> allFacultyMembers = facultyService.getAllActiveFaculty();
        List <Faculty> facultyMembersWhoNotVotedForTheApplication = new ArrayList<>();
        for (Faculty faculty : allFacultyMembers) {
            if (voteService.getVoteByFacultyAndApplication(faculty, application) == null) {
                facultyMembersWhoNotVotedForTheApplication.add(faculty);
            }
        }

        if (facultyMembersWhoNotVotedForTheApplication.size() > 0 || facultyService.countAll() == 0) {
            model.addAttribute("AllFacultyMembersMadeVoteForTheApplication", false);
        } else {
            model.addAttribute("AllFacultyMembersMadeVoteForTheApplication", true);
        }

        model.addAttribute("notVotedFacultyMembers", facultyMembersWhoNotVotedForTheApplication);

        return "admin_application_info";
    }

    /**
     * Send an email message to all users exception admin
     */

    @PostMapping("/sendEmailToAll")
    public String sendEmailToAll (@RequestParam("messageText") String messageText, Model model) {
        List<User> allUsers = userService.allUsers();
        for (User user : allUsers) {
           if (user.getUsername().equals(Constant.ADMIN_USER_NAME)) continue;
            sendEmailLow(user.getId(), messageText);
        }

        return "admin_response_email_sent_to_all";
    }

    /**
     * Send an email message to the faculty member
     */

    @PostMapping("/sendEmailToFacultyMember/{id_user_linked_with_faculty_member}/{id_faculty_member}")
    public String sendEmailToFacultyMember (
            @PathVariable(value = "id_user_linked_with_faculty_member") long userId,
            @PathVariable(value = "id_faculty_member") long facultyId,
            @RequestParam("messageText") String messageText,
            Model model) {

        sendEmailLow(userId, messageText);
        model.addAttribute("id_faculty_member", facultyId);
        return "admin_response_email_sent_to_faculty";
    }

    /**
     * Send an email message to the applicant
     */

    @PostMapping("/sendEmailToApplicant/{userId}/{application_id}/{mode}/{id_call}/{order}")
    public String sendEmailToApplicant (@PathVariable(value = "userId") long userId,
                                        @PathVariable(value = "application_id") long applicationId,
                                        @PathVariable(value = "mode") int mode,
                                        @PathVariable(value = "id_call") long idCall,
                                        @PathVariable(value = "order") int order,
                                        @RequestParam("messageText") String messageText,
                                        Model model) {

        model.addAttribute("application_id", applicationId); // for the creating call button on the admin_response_email_sent
        model.addAttribute("modeCall", mode); // for the transferring via the admin_response_email_sent
        model.addAttribute("idCall", idCall); // for the transferring via the admin_response_email_sent
        model.addAttribute("orderCall", order); // for the transferring via the admin_response_email_sent
        sendEmailLow(userId, messageText);
        return "admin_response_email_sent_to_applicant";
    }

    /**
     * Show the extended admin page (reserved)
     */

    @GetMapping("/adminExtended")
    public String adminExtendedProcessing(Model model) {
        User user = userService.getCurrentUser();
        String returnRoute = "redirect:/";
        if (user.getUsername().equals(Constant.ADMIN_USER_NAME)) {
            model.addAttribute("isItAdmin", true);
            model.addAttribute("isItReset", false);
            returnRoute = "admin_extended";
        }

        return returnRoute;
    }

    /**
     * Show empty users:
     * check users who do not connected with application or faculty members.
     * Situation like this may occur in case:
     * user didn't fill a form (application or faculty member account) after
     * the registration process
     */

    @GetMapping("/showEmptyUsers")
    public String checkEmptyUsers (Model model) {

        List<User> usersList = userService.allUsers();
        List<User> emptyUsersList = new ArrayList<>();

        for (User user : usersList) {
            if (user.getId() != 1L) {
                Application applications = applicationService.getByUser(user);
                Faculty facultyMember    = facultyService.getByUser(user);
                if (applications == null && facultyMember == null) {
                    emptyUsersList.add(user);
                }
            }
        }

        if (emptyUsersList.size() == 0) {
            model.addAttribute("isListEmpty", true);
        } else {
            model.addAttribute("isListEmpty", false);
        }

        model.addAttribute("emptyUsers", emptyUsersList);
        model.addAttribute("returnSignature", urlCoder.urlCoder("/showEmptyUsers"));

        return "admin_empty_users";
    }

    /**
     * CRUD: Delete user with ID
     */

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable(value = "id") Long userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            logger.error("deleteUser: User with ID = " + userId + " not found.");
            throw new RuntimeException();
        }
        confirmationTokenService.deleteTokensByUser(user);
        userService.deleteUserById(userId);
        return "redirect:/showEmptyUsers";
    }

    /**
     * Send an email message to the "empty user"
     */

    @PostMapping("/sendEmailToUnknownUserXLite/{id}/{returnSignature}")
    public String sendEmailToFacultyMember (
            @PathVariable(value = "id") Long userId,
            @PathVariable(value = "returnSignature") String returnSignature,
            @RequestParam("messageText") String messageText,
            Model model) {

        sendEmailLow(userId, messageText);
        model.addAttribute("returnSignatureDecoded", returnSignature);

        return "admin_response_email_sent";
    }

    /**
     * Redirect with the signature. Signature is for the navigation matter
     */

    @GetMapping("/redirect/{returnSignature}")
    public String redirect (@PathVariable(value = "returnSignature") String returnSignature, Model model) {
        return "redirect:" + urlCoder.urlDeCoder(returnSignature);
    }

    /**
     * Mail sender
     */

    private void sendEmailLow (long userId, String messageText) {

        User user = userService.findUserById(userId);
        if (user == null) {
            logger.error("sendEmailLow: User with ID = " + userId + " not found.");
            throw new RuntimeException();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUsername());
        message.setFrom(Constant.EMAIL_SENDER_USERNAME);
        message.setReplyTo(Constant.ADMIN_USER_NAME);  //
        message.setSubject("Message from the Academy");

        messageText +=
                "\n\nATTENTION. Please, do not answer on this email. Feel free to contact us by email: " +
                        Constant.ADMIN_USER_NAME + "! Thank you!\n\nBest regards,\nAcademy.";

        message.setText(messageText);

        emailSender.send(message);
    }

}
