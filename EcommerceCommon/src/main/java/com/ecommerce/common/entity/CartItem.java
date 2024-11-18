package com.ecommerce.common.entity;

import com.ecommerce.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends IdBasedEntity{
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Transient
    private float shippingCost;

    public CartItem(Customer customer, Product product, Integer quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
    }

    @Transient
    public float getSubtotal(){
        return product.getDiscountPrice() * quantity;
    }
}
