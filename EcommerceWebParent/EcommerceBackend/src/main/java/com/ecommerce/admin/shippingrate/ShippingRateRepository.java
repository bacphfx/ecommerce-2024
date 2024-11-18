package com.ecommerce.admin.shippingrate;

import com.ecommerce.admin.paging.SearchRepository;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    public ShippingRate findByCountryAndState(Integer countryId, String state);

    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    public void updateCODSupported(Integer id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% OR sr.state LIKE %?1%")
    public Page<ShippingRate> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
