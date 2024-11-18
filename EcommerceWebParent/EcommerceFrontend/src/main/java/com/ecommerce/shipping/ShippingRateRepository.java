package com.ecommerce.shipping;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
    public ShippingRate findByCountryAndState(Country country, String state);
}
