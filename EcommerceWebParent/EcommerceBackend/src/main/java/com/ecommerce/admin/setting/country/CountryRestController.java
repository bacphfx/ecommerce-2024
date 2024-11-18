package com.ecommerce.admin.setting.country;

import com.ecommerce.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryRestController {
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/list")
    public List<Country> listAll(){
        return countryRepository.findAllByOrderByNameAsc();
    }

    @PostMapping("/save")
    public String saveCountry(@RequestBody Country country){
        Country savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCountry(@PathVariable Integer id){
        countryRepository.deleteById(id);
    }
}
