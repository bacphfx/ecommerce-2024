package com.ecommerce.admin.product;

import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct(){
        Brand brand = entityManager.find(Brand.class,37);
        Category category = entityManager.find(Category.class, 5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("A good desktop");
        product.setFullDescription("This is a test full description");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(1000);
        product.setCost(1888);
        product.setEnabled(true);
        product.setInStock(10);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
    }

    @Test
    public void testSaveProductWithImage(){
        Integer id = 1;
        Product product = productRepository.findById(1).get();

        product.setMainImage("main image.png");
        product.addExtraImage("extra_1.png");
        product.addExtraImage("extra_2.png");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getImages().size()).isEqualTo(2);
    }

    @Test
    public void testSaveProductWithDetails(){
        Integer id = 1;
        Product product = productRepository.findById(1).get();

        product.addDetails("Device memory", "128 GB");
        product.addDetails("CPU model", "MediaTek");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails().size()).isEqualTo(2);
    }


}
