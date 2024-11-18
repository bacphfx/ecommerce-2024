package com.ecommerce.common.entity.order;

import com.ecommerce.common.entity.IdBasedEntity;
import com.ecommerce.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends IdBasedEntity {

    private int quantity;

    private float productCost;

    private float shippingCost;

    private float unitPrice;

    private float subtotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
