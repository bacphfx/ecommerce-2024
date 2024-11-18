package com.ecommerce.common.entity.product;

import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends IdBasedEntity {

    @Column(unique = true, length = 256, nullable = false)
    private String name;

    @Column(unique = true, length = 256, nullable = false)
    private String alias;

    @Column(name = "short_description",length = 512, nullable = false)
    private String shortDescription;

    @Column(name = "full_description",length = 4096, nullable = false)
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    private boolean enabled;

    @Column(name = "in_stock")
    private Integer inStock;

    private float cost;
    private float price;

    @Column(name = "discount_percent")
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Product(Integer id) {
        this.id = id;
    }

    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetails(Integer id, String name, String value){
        this.details.add(new ProductDetail(id, name, value, this));
    }

    public void addDetails(String name, String value){
        this.details.add(new ProductDetail(name, value, this));
    }

    @Transient
    public String getMainImagePath(){
        if (this.id == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    public boolean containsImageName(String fileName) {
        Iterator<ProductImage> iterator = images.iterator();
        while (iterator.hasNext()){
            ProductImage image = iterator.next();
            if (image.getName().equals(fileName)) return true;
        }
        return false;
    }

    @Transient
    public String getShortName(){
        if (name.length() > 70){
            return name.substring(0,70).concat("...");
        }
        return name;
    }

    @Transient
    public Float getDiscountPrice(){
        if (discountPercent > 0){
            return price * (1 - discountPercent/100);
        }
        return this.price;
    }
}
