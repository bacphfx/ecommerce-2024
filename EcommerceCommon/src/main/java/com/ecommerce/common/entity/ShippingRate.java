package com.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRate extends IdBasedEntity{
    private float rate;

    private int days;

    @Column(name = "code_supported")
    private boolean codSupported;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, length = 45)
    private String state;
}
