package com.ecommerce.admin.customer;

import com.ecommerce.admin.paging.SearchRepository;
import com.ecommerce.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends SearchRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE CONCAT(c.id, ' ', c.email, ' ', c.firstName, ' ', c.lastName) LIKE %?1%")
    public Page<Customer> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT c FROM  Customer c WHERE c.email = ?1")
    public Customer findByEmail(String email);

    public Long countById(Integer id);
}
