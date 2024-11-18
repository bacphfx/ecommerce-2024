package com.ecommerce.admin.setting.state;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTest {
    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateState(){
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);
        State state = new State("Ho Chi Minh", country);
        State savedState = stateRepository.save(state);

        assertThat(savedState).isNotNull();
        assertThat(savedState.getId()).isGreaterThan(0);
    }

    @Test
    public void testListStatesByCountry(){
        Integer countryId = 1;
        Country country = entityManager.find(Country.class, countryId);
        List<State> states = stateRepository.findAllByCountryOrderByNameAsc(country);
        states.forEach(System.out::println);
        assertThat(states.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateState(){
        Integer id =1;
        State state = stateRepository.findById(id).get();
        state.setName("Washington");
        State updatedState = stateRepository.save(state);
        assertThat(updatedState.getName()).isEqualTo("Washington");
    }
}
