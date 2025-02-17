package com.ecommerce.address;

import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    public List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    public Address findByIdAndCustomer(Integer addressId, Integer customerId);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    public void deleteByIdAndCustomer(Integer addressId, Integer customerId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
    public void setDefaultAddress(Integer id);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id <> ?1 AND a.customer.id = ?2")
    public void setNonDefaultForOthers(Integer id, Integer customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultForShipping = true")
    public Address findDefaultByCustomer(Integer customerId);
}
