package com.ecommerce.cart;

import com.ecommerce.Utility;
import com.ecommerce.address.AddressService;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.exception.CustomerNotFoundException;
import com.ecommerce.customer.CustomerService;
import com.ecommerce.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ShippingRateService shippingRateService;

    @GetMapping("/cart")
    public String getCart(HttpServletRequest request, Model model) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = shoppingCartService.listCartItems(authenticatedCustomer);

        Float estimatedTotal = 0f;
        for (CartItem cartItem : cartItems) {
            estimatedTotal += cartItem.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(authenticatedCustomer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null){
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(authenticatedCustomer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findByEmail(email).get();
    }
}
