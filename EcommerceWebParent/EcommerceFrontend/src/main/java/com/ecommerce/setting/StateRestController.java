package com.ecommerce.setting;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;
import com.ecommerce.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId){
        List<State> states = stateRepository.findAllByCountryOrderByNameAsc(new Country(countryId));
        List<StateDTO> result = new ArrayList<>();

        for (State state : states){
            result.add(new StateDTO(state.getId(), state.getName()));
        }
        return result;
    }
}
