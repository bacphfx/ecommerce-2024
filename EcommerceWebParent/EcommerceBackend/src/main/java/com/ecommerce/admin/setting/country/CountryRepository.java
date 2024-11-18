package com.ecommerce.admin.setting.country;

import com.ecommerce.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    public List<Country> findAllByOrderByNameAsc();
}
