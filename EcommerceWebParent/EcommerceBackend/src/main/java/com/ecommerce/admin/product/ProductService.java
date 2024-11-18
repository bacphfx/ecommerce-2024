package com.ecommerce.admin.product;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public void listByPage(int pageNum, int pageSize, PagingAndSortingHelper helper,
                                    Integer categoryId) {
        Pageable pageable = helper.createPageable(pageNum, pageSize);
        String keyword = helper.getKeyword();

        Page<Product> page = null;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            } else {
                page = productRepository.findAll(keyword, pageable);
            }
        } else {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.findAllByCategory(categoryId, categoryIdMatch, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        }
        helper.updateModelAttribute(pageNum, pageSize, page);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            product.setAlias(product.getName().toLowerCase().replaceAll(" ", "-"));
        } else {
            product.setAlias(product.getAlias().toLowerCase().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        return productRepository.save(product);
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());
        productRepository.save(productInDB);
    }

    public String checkUnique(Integer id, String name) {
        Product productByName = productRepository.findByName(name);
        if (productByName == null) {
            return "OK";
        }
        boolean isCreatingNew = (id == null || id == 0);
        if (isCreatingNew || !productByName.getId().equals(id)) {
            return "Duplicate";
        }
        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledProduct(id, enabled);
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find product with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    public Product getById(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find product with ID: " + id);
        }
    }
}
