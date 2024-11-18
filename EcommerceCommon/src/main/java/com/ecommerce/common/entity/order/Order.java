package com.ecommerce.common.entity.order;

import com.ecommerce.common.entity.AbstractAddress;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends AbstractAddress {
    @Column(nullable = false, length = 45)
    private String country;

    @Column(name = "order_time")
    private Date orderTime;

    @Column(name = "shipping_cost")
    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;

    private int deliverDays;
    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public void copyAddressFromCustomer(){
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setState(customer.getState());
        setCountry(customer.getCountry().getName());
        setZipCode(customer.getZipCode());
    }

    @Transient
    public String getDestination(){
        String destination = "";
        if (city != null) destination += city + ", ";
        if (state != null) destination += state + ", ";
        destination += country;
        return destination;
    }

    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setState(address.getState());
        setCountry(address.getCountry().getName());
        setZipCode(address.getZipCode());
    }

    @Transient
    public String getShippingAddress() {
        String address = firstName;
        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if (addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (!city.isEmpty()) address += ", " + city;
        if (!state.isEmpty()) address += ", " + state;
        address += ", " + country;
        if (!zipCode.isEmpty()) address += ". Zip code:  " + zipCode;
        if (!phoneNumber.isEmpty()) address += ". Phone number:  " + phoneNumber;

        return address;
    }

    @Transient
    public String getDeliverDateOnForm(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(deliverDate);
    }
}
