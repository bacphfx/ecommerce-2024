package com.ecommerce.customer;

import com.ecommerce.common.entity.AuthenticationType;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCustomer(){
        Integer countryId = 234;
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Pham");
        customer.setLastName("Bac");
        customer.setPassword("123456");
        customer.setEmail("hobac@gmail.com");
        customer.setPhoneNumber("0123456789");
        customer.setAddressLine1("112 Y La");
        customer.setCity("Ha Noi");
        customer.setState("Ha Noi");
        customer.setZipCode("100000");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateCustomer2(){
        Integer countryId = 242;
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Ngan");
        customer.setLastName("Do");
        customer.setPassword("123456");
        customer.setEmail("ngando@gmail.com");
        customer.setPhoneNumber("0123456789");
        customer.setAddressLine1("112 Y La");
        customer.setCity("Ha Noi");
        customer.setState("Ha Noi");
        customer.setZipCode("100000");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCustomers(){
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out::println);
        assertThat(customers).hasSizeGreaterThan(0);
    }

    @Test
    public void testUpdateCustomer(){
        Integer customerId = 1;
        String lastName = "Ho Bac";
        Customer customer = customerRepository.findById(customerId).get();

        customer.setLastName(lastName);
        customer.setEnabled(true);
        customer.setVerificationCode("code123");

        Customer updatedCustomer = customerRepository.save(customer);

        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testFindByEmail(){
        String email = "hobac@gmail.com";
        Customer customer = customerRepository.findByEmail(email).get();
        System.out.println(customer);
        assertThat(customer.getEmail()).isEqualTo(email);
    }

    @Test
    public void testFindByVerificationCode(){
        String code = "code123";
        Customer customer = customerRepository.findByVerificationCode(code);
        System.out.println(customer);
        assertThat(customer.getVerificationCode()).isEqualTo(code);
    }

    @Test
    public void testDeleteCustomer(){
        Integer id = 2;
        customerRepository.deleteById(id);
        Optional<Customer> customer = customerRepository.findById(id);
        assertThat(customer).isNotPresent();
    }

    @Test
    public void testEnableCustomer(){
        Integer id = 2;
        customerRepository.enable(id);
        Customer customer = customerRepository.findById(id).get();
        assertThat(customer.isEnabled()).isTrue();
    }

    @Test
    public void testUpdateAuthenticationType(){
        Integer id = 1;
        customerRepository.updateAuthenticationType(id, AuthenticationType.DATABASE);

        Customer customer = customerRepository.findById(id).get();
        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
    }
}
