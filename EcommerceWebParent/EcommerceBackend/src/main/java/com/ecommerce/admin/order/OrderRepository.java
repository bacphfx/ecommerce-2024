package com.ecommerce.admin.order;

import com.ecommerce.admin.paging.SearchRepository;
import com.ecommerce.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends SearchRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE o.firstName LIKE %?1% OR " +
            "o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR " +
            "o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR " +
            "o.city LIKE %?1% OR o.state LIKE %?1% OR " +
            "o.country LIKE %?1% OR o.zipCode LIKE %?1% OR " +
            "o.customer.firstName LIKE %?1% OR o.customer.lastName LIKE %?1%")
    public Page<Order> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
