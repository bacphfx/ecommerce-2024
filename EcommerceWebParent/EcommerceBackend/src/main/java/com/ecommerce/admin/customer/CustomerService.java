package com.ecommerce.admin.customer;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.setting.country.CountryRepository;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void listByPage(int pageNum, int pageSize, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, pageSize, customerRepository);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled){
        customerRepository.updateEnabledStatus(id, enabled);
    }

    public Customer getById(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new CustomerNotFoundException("Could not find customer with id: " + id);
        }
    }

    public List<Country> listAllCountries(){
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(Integer id, String email){
        Customer existCustomer = customerRepository.findByEmail(email);
        if (existCustomer != null && existCustomer.getId() != id){
            return false;
        }
        return true;
    }

    public void saveCustomer(Customer customerInForm){
        Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
        if(!customerInForm.getPassword().isEmpty()){
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }
        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerRepository.save(customerInForm);
    }

    public void deleteCustomer(Integer id) throws CustomerNotFoundException {
        Long countById = customerRepository.countById(id);
        if (countById == null || countById == 0){
            throw new CustomerNotFoundException("Could not find customer with Id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
