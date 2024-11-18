package com.ecommerce.product;

import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, int pageSize, Integer categoryId) {
        String categoryIDMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        return productRepository.listByCategory(categoryId, categoryIDMatch, pageable);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null){
            throw new ProductNotFoundException("Could not find product with alias: " + alias);
        }
        return product;
    }

    public Page<Product> search(String keyword, int pageNum, int pageSize){
        Pageable pageable = PageRequest.of(pageNum -1, pageSize);
        return productRepository.search(keyword, pageable);
    }
}
