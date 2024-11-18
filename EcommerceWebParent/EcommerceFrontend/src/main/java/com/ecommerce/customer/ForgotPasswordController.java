package com.ecommerce.customer;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;
import com.ecommerce.setting.EmailSettingBag;
import com.ecommerce.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    @Autowired
    CustomerService customerService;

    @Autowired
    SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequest(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteUrl(request) + "/reset_password?token=" + token;
            sendResetPasswordEmail(link, email);
            model.addAttribute("message", "We have sent a request password link to your email. Please check.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Could not send email!");
        }
        return "customer/forgot_password_form";
    }

    private void sendResetPasswordEmail(String link, String email)
            throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

        String toAddress = email;
        String subject = "Here is the link to reset your password";
        String content = "<p>Hello,</p>" +
                "<p>You have requested to reset your password,</p>" +
                "<p>Click the link below to change your password:</p>" +
                "<p><a href=\"" + link + "\">Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your pass word," +
                "or you have not made this request</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("title", "Invalid token");
            model.addAttribute("pageTitle", "Invalid token");
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            customerService.updatePassword(token, password);
            model.addAttribute("pageTitle", "Reset Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password");
            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("message", "Invalid token");
            model.addAttribute("pageTitle", e.getMessage());
            return "message";
        }
    }
}
