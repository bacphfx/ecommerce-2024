package com.ecommerce.security;

import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Customer> optional = customerRepository.findByEmail(email);
        if (optional.isEmpty()){
            throw new UsernameNotFoundException("No customer found with email: " + email);
        }
        Customer customer = optional.get();
        return new CustomerUserDetails(customer);
    }
}
