package com.ecommerce.admin.customer;

import com.ecommerce.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(@Param("email") String email,
                                      @Param("id") Integer id){
        return customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
