package com.ecommerce.admin.setting.country;

import com.ecommerce.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCountry(){
        Country country = new Country("Vietnam", "VN");
        Country savedCountry = countryRepository.save(country);

        assertThat(savedCountry).isNotNull();
        assertThat(savedCountry.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries(){
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        countries.forEach(System.out::println);
        assertThat(countries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry(){
        Integer id =1;
        Country country = countryRepository.findById(id).get();
        country.setName("United State Of America");
        country.setCode("USA");
        Country updatedCountry = countryRepository.save(country);
        assertThat(updatedCountry.getName()).isEqualTo("United State Of America");
    }
}
