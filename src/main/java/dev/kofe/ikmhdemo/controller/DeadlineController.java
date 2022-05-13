package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.model.Deadline;
import dev.kofe.ikmhdemo.service.DeadlineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Deadline matter controller
 */

@Controller
public class DeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    private Logger logger = LogManager.getLogger(DeadlineController.class);

    /**
     * Show the deadline page
     */

    @GetMapping("/showDeadlinePage")
    public String showDeadlinePage(Model model) {

        Deadline deadline = deadlineService.getDeadlineById(1L);

        if (deadline == null) {
                logger.error("showDeadlinePage: the deadline not found by ID = 1L");
                throw new RuntimeException();
        }

        model.addAttribute("deadline", deadline);

        return "admin_deadline_editor";
    }

    /**
     * Save deadline
     */

    @PostMapping("/saveDeadline")
    public String saveDeadline(
            @RequestParam("id") long id,
            @RequestParam("deadlineDate") String date,
            @RequestParam("deadlineTime") String time,
            @RequestParam("notes")        String notes,
            Model model) {

        Deadline deadline = deadlineService.getDeadlineById(id);

        deadline.setNotes(notes);

        deadline.setDeadlineDate(java.sql.Date.valueOf(date));
        String timeFormat = (time.length() == 5) ? (time + ":00") : time;

        deadline.setDeadlineTime(java.sql.Time.valueOf(timeFormat));

        deadlineService.saveDeadline(deadline);

        model.addAttribute("result", "The new deadline has been updated!");
        model.addAttribute("deadline", deadline);

        return "admin_deadline_editor";
    }

}
