package com.ecommerce.cart;

import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCartItem(){
        Integer customerId = 10;
        Integer productId = 4;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cartItemRepository.save(cartItem);

        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreate2CartItem(){
        Integer customerId = 2;

        Customer customer = entityManager.find(Customer.class, customerId);
        CartItem cartItem1 = new CartItem();
        cartItem1.setCustomer(customer);
        cartItem1.setProduct(new Product(5));
        cartItem1.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCustomer(new Customer(6));
        cartItem2.setProduct(new Product(8));
        cartItem2.setQuantity(3);

        Iterable<CartItem> iterable = cartItemRepository.saveAll(List.of(cartItem1, cartItem2));


        assertThat(iterable).size().isGreaterThan(0);
    }

    @Test
    public void testListByCustomer(){
        Integer customerId = 10;
        List<CartItem> cartItems = cartItemRepository.findByCustomer(new Customer(customerId));
        cartItems.forEach(System.out::println);
        assertThat(cartItems.size()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndProduct(){
        Integer customerId = 1;
        Integer productId = 1;
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem).isNotNull();

        System.out.println(cartItem);
    }

    @Test
    public void testUpdateQuantity(){
        Integer customerId = 1;
        Integer productId = 1;
        Integer quantity = 3;
        cartItemRepository.updateQuantity(quantity, customerId, productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(cartItem.getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void deteleCartItem(){
        Integer customerId = 1;
        Integer productId = 1;
        cartItemRepository.deleteByCustomerAndProduct(customerId, productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(cartItem).isNull();

    }
}
