package com.ecommerce.admin.setting.country;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.MediaType;

import com.ecommerce.common.entity.Country;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        assertThat(countries).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testCreateCountries() throws Exception {
        String url = "/countries/save";
        String countryName = "Canada";
        String countryCode = "CA";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(country))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(response);
        Country savedCountry =  countryRepository.findById(countryId).get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testUpdateCountries() throws Exception {
        String url = "/countries/save";
        Integer countryId = 4;
        String countryName = "Germany";
        String countryCode = "GER";
        Country country = new Country(countryId, countryName, countryCode);

       mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(country))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));


        Country savedCountry =  countryRepository.findById(countryId).get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "bacph18@gmail.com", password = "hihi", roles = "Admin")
    public void testDeleteCountry() throws Exception {
        Integer id = 4;
        String url = "/countries/delete/" + id;
        mockMvc.perform(delete(url).with(csrf()))
                .andExpect(status().isOk());

        Optional<Country> findById = countryRepository.findById(id);

        assertThat(findById).isNotPresent();
    }
}
