package com.ecommerce.address;

import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer addressId, Integer customerId) {
        return addressRepository.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Integer addressId, Integer customerId) {
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Integer addressId, Integer customerId) {
        if (addressId > 0) {
            addressRepository.setDefaultAddress(addressId);
        }
        addressRepository.setNonDefaultForOthers(addressId, customerId);
    }

    public Address getDefaultAddress(Customer customer){
        return addressRepository.findDefaultByCustomer(customer.getId());
    }
}
