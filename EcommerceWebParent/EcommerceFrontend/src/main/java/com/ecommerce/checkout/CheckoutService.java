package com.ecommerce.checkout;

import com.ecommerce.cart.CartItemRepository;
import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {
    @Autowired
    CartItemRepository cartItemRepository;
    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate){
        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(productTotal + shippingCostTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());

        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float cost = 0.0f;
        for (CartItem cartItem : cartItems){
            float shippingCost = cartItem.getProduct().getWeight() * cartItem.getQuantity() * shippingRate.getRate();
            cartItem.setShippingCost(shippingCost);
            cost += shippingCost;
        }
        return cost;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem cartItem : cartItems){
            total += cartItem.getSubtotal();
        }
        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem cartItem : cartItems){
            cost += cartItem.getQuantity() * cartItem.getProduct().getCost();
        }
        return cost;
    }
}
