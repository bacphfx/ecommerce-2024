package com.ecommerce.common.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractAddressWithCountry extends AbstractAddress{
    @ManyToOne
    @JoinColumn(name = "country_id")
    protected Country country;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        String address = getFullName();
        if (addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (!city.isEmpty()) address += ", " + city;
        if (!state.isEmpty()) address += ", " + state;
        address += ", " + country.getName();
        if (!zipCode.isEmpty()) address += ". Zip code:  " + zipCode;
        if (!phoneNumber.isEmpty()) address += ". Phone number:  " + phoneNumber;

        return address;
    }
}
