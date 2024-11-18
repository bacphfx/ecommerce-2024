package com.ecommerce.cart;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;
import com.ecommerce.common.exception.ShoppingCartException;
import com.ecommerce.common.exception.ProductNotFoundException;
import com.ecommerce.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public ResponseEntity<String> addProductToCart(@PathVariable("productId") Integer productId,
                                                   @PathVariable("quantity") Integer quantity,
                                                   HttpServletRequest request) {
        try {
            Customer authenticatedCustomer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, authenticatedCustomer);

            String successMessage = "Success! There are (is) " + updatedQuantity + " item(s) added to your shopping cart.";
            return ResponseEntity.ok(successMessage);

        } catch (CustomerNotFoundException e) {
            String errorMessage = "You must login to add this product to cart";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);

        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (ShoppingCartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.findByEmail(email).get();
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public ResponseEntity<String> updateQuantity(@PathVariable("productId") Integer productId,
                                                 @PathVariable("quantity") Integer quantity,
                                                 HttpServletRequest request) {
        try {
            Customer authenticatedCustomer = getAuthenticatedCustomer(request);
            Float subtotal = shoppingCartService.updateQuantity(productId, quantity, authenticatedCustomer);
            return ResponseEntity.ok(String.valueOf(subtotal));
        } catch (CustomerNotFoundException e) {
            String errorMessage = "You must login to update quantity of this product in cart";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        } catch (ShoppingCartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<String> removeProduct(@PathVariable("productId") Integer productId,
                                                 HttpServletRequest request){
        try {
            Customer authenticatedCustomer = getAuthenticatedCustomer(request);
            shoppingCartService.removeProduct(productId, authenticatedCustomer);
            return ResponseEntity.ok("The product has been removed from your cart");
        } catch (CustomerNotFoundException e) {
            String errorMessage = "You must login to remove this product from cart";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);        }
    }
}
