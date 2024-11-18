package com.ecommerce.customer;

import com.ecommerce.common.entity.AuthenticationType;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;
import com.ecommerce.setting.CountryRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Optional<Customer> optional = customerRepository.findByEmail(email);

        if (optional.isPresent()) return false;

        return true;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        customerRepository.save(customer);

    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verifyCustomer(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enable(customer.getId());
            return true;
        }
    }

    public void updateAuthentication(Customer customer, AuthenticationType type) {
        if (!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType type) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(type);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setZipCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));

        customerRepository.save(customer);
    }

    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);
            String lastName = name.replaceFirst(firstName + " ", "");
            customer.setLastName(lastName);
        }
    }

    public void updateCustomer(Customer customerInForm) {
        Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
        if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if (!customerInForm.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
                customerInForm.setPassword(encodedPassword);
            } else {
                customerInForm.setPassword(customerInDB.getPassword());
            }
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }
        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
        customerRepository.save(customerInForm);
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Optional<Customer> optional = customerRepository.findByEmail(email);
        if (optional.isPresent()){
            Customer customer = optional.get();
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with email: " + email);
        }
    }

    public Customer getByResetPasswordToken(String token){
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String password) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer == null){
            throw new CustomerNotFoundException("Customer not found: Invalid token");
        }
        customer.setPassword(password);
        customer.setResetPasswordToken(null);
        encodePassword(customer);
        customerRepository.save(customer);
    }
}
