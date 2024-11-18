package com.ecommerce.customer;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.security.CustomerUserDetails;
import com.ecommerce.security.oauth.CustomerOAuth2User;
import com.ecommerce.setting.EmailSettingBag;
import com.ecommerce.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Country> countries = customerService.listAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model,
                                 HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);

        sendVerificationEmail(request, customer);

        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("message", "Registration Succeeded");
        return "/register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

        String toAddress = customer.getEmail();
        String subject = emailSettingBag.getCustomerVerificationSubject();
        String content = emailSettingBag.getCustomerVerificationContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[NAME]]", customer.getFullName());
        String verifyURL = Utility.getSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model){
        boolean verify = customerService.verifyCustomer(code);

        return "register/" + (verify ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request){
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        Optional<Customer> optional = customerService.findByEmail(email);
        optional.ifPresent(customer -> model.addAttribute("customer", customer));
        List<Country> countries = customerService.listAllCountries();
        model.addAttribute("countries", countries);
        return "customer/account_form";
    }



    @PostMapping("/update_account_details")
    public String updateAccountDetails(RedirectAttributes redirectAttributes, Customer customer,
                                       HttpServletRequest request){
        customerService.updateCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated successfully");
        updateNameForAuthenticatedCustomer(customer, request);

        String redirectParam = request.getParameter("redirect");
        String redirectUrl = "redirect:/account_details";
        if ("addresses".equals(redirectParam)) {
            redirectUrl = "redirect:/addresses";
        } else if ("cart".equals(redirectParam)) {
            redirectUrl = "redirect:/cart";
        } else if ("checkout".equals(redirectParam)) {
            redirectUrl = "redirect:/addresses?redirect=checkout";
        }

        return redirectUrl;
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            CustomerUserDetails userDetails = getCustomerUserDetails(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User customerOAuth2User= (CustomerOAuth2User) token.getPrincipal();
            customerOAuth2User.setFullName(customer.getFullName());
        }
    }

    private CustomerUserDetails getCustomerUserDetails(Object principal){
        CustomerUserDetails userDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken){
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }
        return userDetails;
    }
}
