package com.ecommerce.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/customers/check_email")
    public String checkUniqueEmail(@RequestParam String email){
        return customerService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
