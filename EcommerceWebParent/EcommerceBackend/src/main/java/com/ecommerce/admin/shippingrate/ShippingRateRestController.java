package com.ecommerce.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {
    @Autowired
    private ShippingRateService shippingRateService;

    @PostMapping("/get_shipping_cost")
    public ResponseEntity<String> getShippingCost(Integer productId, Integer countryId, String state){
        try {
            float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
            return ResponseEntity.ok(String.valueOf(shippingCost));
        } catch (ShippingRateNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
