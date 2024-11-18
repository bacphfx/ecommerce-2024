package com.ecommerce.setting;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
    public List<State> findAllByCountryOrderByNameAsc(Country country);
}
