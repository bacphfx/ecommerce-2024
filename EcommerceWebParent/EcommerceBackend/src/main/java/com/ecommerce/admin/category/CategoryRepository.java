package com.ecommerce.admin.category;

import com.ecommerce.admin.paging.SearchRepository;
import com.ecommerce.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c where c.parent.id is NULL")
    public List<Category> findRootCategories(Sort sort);

    @Query("SELECT c FROM Category c where c.parent.id is NULL")
    public Page<Category> findRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category c where c.name like %?1%")
    public Page<Category> search(String keyword, Pageable pageable);

    public Category findByName(String name);

    public Long countById(Integer id);

    public Category findByAlias(String alias);

    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);
}
