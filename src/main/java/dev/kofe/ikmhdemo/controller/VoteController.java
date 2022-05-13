package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.model.*;
import dev.kofe.ikmhdemo.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vote matter controller
 */

@Controller
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private DeadlineService deadlineService;

    private Logger logger = LogManager.getLogger(VoteController.class);

    /**
     * Show page with list of applications to vote
     * (applicant makes an application, so application == applicant for the system)
     */

    @GetMapping("/showVotePageListOfApplicants")
    public String showVotePageListOfApplicants (Model model) {

        User user = userService.getCurrentUser();
        if (user.getAcademyMember() != AcademyMember.FACULTY) {
            return "index";
        }

        // Addition check of deadline matters
        Deadline deadline = deadlineService.getDeadlineById(1L);
        if (deadline == null) {
            logger.error("showVotePageListOfApplicants: deadline (with ID = 1L) not found. Probably it is not initialized");
            throw new RuntimeException();
        }

        String deadlineString = deadline.getDeadlineDate() + "T" + deadline.getDeadlineTime() + ".000Z";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime dateWithTime = ZonedDateTime.parse(deadlineString, formatter);
        long deadlineMilliseconds = dateWithTime.toInstant().toEpochMilli(); // this
        long currentMilliseconds = Instant.now().toEpochMilli();
        if (currentMilliseconds - deadlineMilliseconds <= 0) {
            // deadline is not passed => "illegal call"
            return "index";
        }

        // so, next
        Faculty faculty = facultyService.getByUser(user);

        //@ToDo transmit a message
        if (faculty == null) {
            // faculty has not filled a card yet
            return "index";
        }

        // Everything OK, let's do it
        //List<Application> applications = applicationService.getAllApplications();
        List<Application> applications = applicationService.getAllWhoDidNotMissedTheDeadline();
        for (Application applicant : applications) {
            Vote foundVote = voteService.getVoteByFacultyAndApplication(faculty, applicant);
            if (foundVote != null) {
                applicant.setVotedByTheFaculty(true);
                applicant.setVoteValueByTheFaculty(foundVote.getVoteValue());
            } else {
                applicant.setVotedByTheFaculty(false);
            }
        }

        model.addAttribute("faculty", faculty);
        model.addAttribute("applications", applications);

        return "vote_list_of_applicants";
    }

    /**
     * Vote for the application
     */

    @GetMapping("/voteApplicant/{id}")
    public String voteApplicant (@PathVariable(value = "id") long id, Model model) {

        Application application = applicationService.getById(id);
        if (application == null) {
            logger.error("voteApplicant: application with ID: " + id + " not found");
            throw new RuntimeException();
        }

        User user = userService.getCurrentUser();
        if (user == null) {
            logger.error("voteApplicant: current user not retrieved");
            throw new RuntimeException();
        }

        if (user.getAcademyMember() != AcademyMember.FACULTY) {
            return "index";
        }

        Faculty faculty = facultyService.getByUser(user);
        if (faculty == null) {
            logger.error("voteApplicant: faculty-by-user not retrieved");
            throw new RuntimeException();
        }

        Vote vote = voteService.getVoteByFacultyAndApplication(faculty, application);

        if (vote == null) {
            vote = new Vote();
            vote.setFaculty(faculty);
            vote.setApplication(application);
            voteService.saveVote(vote);
        }

        model.addAttribute("faculty", faculty);
        model.addAttribute("applicant", application);
        model.addAttribute("vote", vote);

        return "vote_applicant";
    }

    /**
     * Save the vote
     */

    @PostMapping("/saveVote")
    public String saveVote (@ModelAttribute("vote") Vote vote,
                            @RequestParam("voteValue") int voteValue) {

        vote.setVoteValue(voteValue);
        voteService.saveVote(vote);

        return "redirect:/showVotePageListOfApplicants";
    }

}



