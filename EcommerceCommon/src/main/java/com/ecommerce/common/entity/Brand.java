package com.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends IdBasedEntity{

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    @ToString.Exclude
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand(String name) {
        this.name = name;
        this.logo = "brand-logo.png";
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.logo = "brand-logo.png";
    }

    @Transient
    public String getLogoPath(){
        if (this.id == null) return "/images/image-thumbnail.png";
        return "/brand-logo/" + this.id + "/" + this.logo;
    }
}
