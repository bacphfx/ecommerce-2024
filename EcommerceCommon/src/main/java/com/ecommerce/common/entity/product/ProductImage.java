package com.ecommerce.common.entity.product;

import com.ecommerce.common.Constants;
import com.ecommerce.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends IdBasedEntity {
    private String name;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(Integer id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath() {
        return Constants.S3_BASE_URI + "/product-images/" + product.getId() + "/extras/" + this.name;
    }
}
