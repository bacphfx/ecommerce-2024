package com.ecommerce.cart;

import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.exception.ShoppingCartException;
import com.ecommerce.common.exception.ProductNotFoundException;
import com.ecommerce.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ProductNotFoundException, ShoppingCartException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        Integer inStock = product.getInStock();
        if (quantity > inStock) {
            throw new ShoppingCartException("Requested quantity exceeds available stock");
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
        Integer updatedQuantity = (cartItem != null) ? cartItem.getQuantity() + quantity : quantity;
        if (updatedQuantity > inStock) {
            throw new ShoppingCartException("You already have "
                    + cartItem.getQuantity()
                    + " products in your cart and you cannot add to cart more than in stock");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public Float updateQuantity(Integer productId, Integer newQuantity, Customer customer) throws ProductNotFoundException, ShoppingCartException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
        if (cartItem == null) {
            throw new ShoppingCartException("Cart item not found for the specified product and customer.");
        }

        Integer inStock = product.getInStock();

        Integer oldQuantity = cartItem.getQuantity();
        if (newQuantity > inStock && newQuantity > oldQuantity) {
            throw new ShoppingCartException("Requested quantity exceeds available stock. There are " + inStock + " products in stock");
        }
        cartItemRepository.updateQuantity(newQuantity, customer.getId(), productId);
        float subtotal = product.getDiscountPrice() * newQuantity;
        return subtotal;
    }

    public void removeProduct(Integer productId, Customer customer){
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer){
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
