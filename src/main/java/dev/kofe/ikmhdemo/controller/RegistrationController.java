package dev.kofe.ikmhdemo.controller;

import dev.kofe.ikmhdemo.config.Constant;
import dev.kofe.ikmhdemo.model.AcademyMember;
import dev.kofe.ikmhdemo.model.ConfirmationToken;
import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.repo.ConfirmationTokenRepository;
import dev.kofe.ikmhdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.Valid;

/**
 * Registration matter controller
 */

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Show the registration page
     */

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    /**
     * Show the reset password page: origin page
     */

    @GetMapping("/resetpassword")
    public String resetPassword(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("askForEmail", "true");
        return "reset_password_form";
    }

    /**
     * Reset password: second stage
     */

    @RequestMapping(value="/do-reset-password", method = {RequestMethod.GET, RequestMethod.POST})
    public String doResetPassword(Model model, @RequestParam("token") String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            // it's OK, get token's user
            User user = userService.findUserByUsernameIgnoreCase(token.getUser().getUsername());

            token.setUsed(true);

            confirmationTokenRepository.save(token);

            // load model
            model.addAttribute("resetPasswordForm", "true");
            model.addAttribute("user", user);

        } else {
            model.addAttribute("error", "Sorry, the link is broken or invalid.");
        }

        return "reset_password_form";
    }

    /**
     * Reset password: final stage
     */

    @PostMapping("/resetPasswordFinal")
    public String resetPasswordFinalAction(@Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userForm);
            model.addAttribute("resetPasswordForm", "true");
            return "reset_password_form";
        }

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("user", userForm);
            model.addAttribute("passwordError", "Passwords don't match. Try again.");
            model.addAttribute("resetPasswordForm", "true");
            return "reset_password_form";
        }

        // everything is OK
        // reset password in the DB

        userService.reWriteUserWithNewPassword(userForm);

        // let's go!
        model.addAttribute("result", "Password has been reset.");

        return "reset_password_form";
    }

    /**
     * Send the confirmation token
     */

    @PostMapping("/sendconfirmtoken")
    public String sendConfirmationTokenInResetPasswordProcess(@ModelAttribute("user") User userForm, Model model) {

        User user = userService.findUserByUsernameIgnoreCase(userForm.getUsername());

        // is the email exist?

        if (user == null) {
            // email (username) is not exist

            model.addAttribute("askForEmail", "true");
            model.addAttribute("result", "Email is not registered in our system. Please, try again.");

        } else {
            // it is OK: email is exist
            // so, prepare and send confirmation token

            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getUsername());
            message.setFrom(Constant.EMAIL_SENDER_USERNAME);
            message.setReplyTo(Constant.ADMIN_USER_NAME);  //
            message.setSubject("Reset password");

            String messageText =
                    "Hello!\n\n" +
                            "To complete the password reset process, please click here: \n\n" +
                            Constant.SERVER_PREFIX + "/" +
                            "do-reset-password?token=" + confirmationToken.getConfirmationToken() +

                            "\n\nATTENTION. Please, do not answer on this email. Feel free to contact us by email: " +
                            Constant.ADMIN_USER_NAME + "! Thank you!" +

                            "\n\nBest regards,\n\nAcademy.";

            message.setText(messageText);
            emailSender.send(message);

            model.addAttribute("result", "Message with the reset link has been sent. Check your email.");

        }

        return "reset_password_form";
    }

    /**
     * A registration process: entering point
     * Note: we use email as a username
     */

    @PostMapping("/registration")
    public String addUser(@Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userForm);
            return "registration";
        }

        if (!userForm.getUsername().equals(userForm.getUsernameConfirm())) {
            model.addAttribute("user", userForm);
            model.addAttribute("usernameError", "Emails don't match");
            return "registration";
        }

        if (!isEmailAddressValid(userForm.getUsername())) {
            model.addAttribute("user", userForm);
            model.addAttribute("usernameError", "Emails is not valid");
            return "registration";
        }

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("user", userForm);
            model.addAttribute("passwordError", "Passwords don't match");
            return "registration";
        }

        if (!userService.saveNewUser(userForm)) {
            model.addAttribute("user", userForm);
            model.addAttribute("usernameError", "User with email '" + userForm.getUsername() + "' already registered");
            return "registration";
        }

        // it is ok, so
        // going to send email with a confirmation code
        // prepare a confirmation token

        // common matter
        ConfirmationToken confirmationToken = new ConfirmationToken(userForm);
        confirmationTokenRepository.save(confirmationToken);

        // mail sender
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Constant.EMAIL_SENDER_USERNAME);
        message.setReplyTo(Constant.ADMIN_USER_NAME);  //
        String messageText = null;

        if (userForm.getAcademyMember() == AcademyMember.STUDENT) {
            // in case of STUDENT the Confirmation letter will send to user personally

            message.setTo(userForm.getUsername());
            message.setSubject("Confirm email");
            messageText =
                    "Hello!\n\n" +
                            "To complete the registration process, please click here: \n\n" +
                            Constant.SERVER_PREFIX + "/" +
                            "confirm-email?token=" + confirmationToken.getConfirmationToken() +
                            "\n\nATTENTION. Please, do not answer on this email. Feel free to contact us by email: " +
                            Constant.ADMIN_USER_NAME + "! Thank you!" +
                            "\n\nBest regards,\n\nAcademy.";

            model.addAttribute("success", "Thanks for registration! Check your email!");
        }

        if (userForm.getAcademyMember() == AcademyMember.FACULTY) {
            // in case of FACULTY the Confirmation letter will send to ADMINISTRATOR

            message.setTo(Constant.EMAIL_ADMINISTRATOR_FOR_CONFIRMATION_FACULTY_REGISTRATION);
            message.setSubject("Confirm faculty member email");
            messageText =
                    "Hello!\n\n" +
                            "Your email is picked as an administrator address at the IKMH Student Application Management System.\n\n" +
                            "We just have received a query to registration of faculty member who have the following email:\n\n" +
                            userForm.getUsername().toString() + "\n\n" +
                            "To confirm the query and complete the registration process, please click here: \n\n" +
                            Constant.SERVER_PREFIX + "/" +
                            "confirm-email?token=" + confirmationToken.getConfirmationToken() +

                            "\n\nATTENTION. Please, do not answer on this email." +

                            "\n\nBest regards,\n\nAcademy.";

            model.addAttribute("success", "Thanks for registration!\nAdministrator will confirm your email soon!");
        }

        // common matter
        message.setText(messageText);
        emailSender.send(message);

        model.addAttribute("user", userForm);

        return "login";
    }

    /**
     * Email validator
     */

    private boolean isEmailAddressValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * Email confirmation
     */

    @RequestMapping(value="/confirm-email", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(Model model, @RequestParam("token") String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        String message = null;

        if(token != null)
        {

            User user = userService.findUserByUsernameIgnoreCase(token.getUser().getUsername());

            token.setUsed(true);

            confirmationTokenRepository.save(token);

            if (user.getAcademyMember() == AcademyMember.STUDENT) {
                if (user.getConfirmed()) {
                    message = "Your email is already confirmed.";
                } else {
                    user.setConfirmed(true);
                    userService.reWriteUser(user);
                    message = "Your email has been confirmed! Thanks!";
                }
            }

            if (user.getAcademyMember() == AcademyMember.FACULTY) {
                if (user.getConfirmed()) {
                    message = "This email is already confirmed.";
                } else {
                    user.setConfirmed(true);
                    userService.reWriteUser(user);
                    message = "Email has been confirmed! Thanks!\nWe're going to send email to faculty member.";

                    // email matter
                    SimpleMailMessage messageToFacultyMember = new SimpleMailMessage();
                    messageToFacultyMember.setTo(user.getUsername());
                    messageToFacultyMember.setFrom(Constant.EMAIL_SENDER_USERNAME);
                    messageToFacultyMember.setReplyTo(Constant.ADMIN_USER_NAME);  //
                    messageToFacultyMember.setSubject("Your email has been confirmed.");
                    String messageText =
                            "Hello!\n\n" +
                                    "Administrator just has been confirmed your email.\n\nWelcome on a board!\n\n" +
                                    "\n\nATTENTION. Please, do not answer on this email. Feel free to contact us by email: " +
                                    Constant.ADMIN_USER_NAME + "! Thank you!" +
                                    "\n\nBest regards,\n\nAcademy.";

                    messageToFacultyMember.setText(messageText);
                    emailSender.send(messageToFacultyMember);
                }
            }

            model.addAttribute("success", message);

        } else {
            model.addAttribute("error", "Sorry, the link is broken or invalid.");
        }

        return "confirmed";
    }

}
