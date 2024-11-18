package com.ecommerce.category;

import com.ecommerce.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC ")
    public List<Category> findAllEnabled();

    @Query("SELECT c FROM Category c WHERE c.alias = ?1 AND c.enabled = true ORDER BY c.name ASC")
    public Category findByAliasEnabled(String alias);
}
