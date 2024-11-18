package com.ecommerce.admin.setting.state;

import com.ecommerce.admin.setting.country.CountryRepository;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testListStateByCountry() throws Exception {
        int countryId = 2;
        String url = "/states/list_by_country/" + countryId;
        MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        State[] states = objectMapper.readValue(jsonResponse, State[].class);
        assertThat(states).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testCreateState() throws Exception {
        String url = "/states/save";
        Integer countryId = 5;
        Country country = countryRepository.findById(countryId).get();

        State state = new State("Paris", country);

        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(state))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer stateId = Integer.parseInt(response);
        State savedState =  stateRepository.findById(stateId).get();
        assertThat(savedState.getName()).isEqualTo("Paris");
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testUpdateState() throws Exception {
        String url = "/states/save";
        Integer stateId = 3;
        String stateName = "Ha Noi";
        State state = stateRepository.findById(stateId).get();
        state.setName(stateName);

       mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(state))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stateId)));
       Optional<State> optional = stateRepository.findById(stateId);
       assertThat(optional).isPresent();

       State savedState = optional.get();
       assertThat(savedState.getName()).isEqualTo(stateName);
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testDeleteState() throws Exception {
        Integer id = 5;
        String url = "/states/delete/" + id;
        mockMvc.perform(delete(url).with(csrf()))
                .andExpect(status().isOk());

        Optional<State> findById = stateRepository.findById(id);

        assertThat(findById).isNotPresent();
    }
}
