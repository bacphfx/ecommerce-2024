package com.ecommerce.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class PaypalApiTest{
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AYtXopWFssLT86UNYCr4jfAMNtbrX7WfY7BYcOSlX5Eq-3wgfV_xu8TmsYT35ihptfLmndtPaIXqd6Jd";
    private static final String SECRET_KEY = "EOvnR8uHHJFSUUOjdGaC5Hyq2ubq597bOuCyNTfMB3biD6fo1Z_kL92woxoctSr5-9rs3rrCLTivjHEf";

    @Test
    public void testGetOrderDetails(){
        String orderId = "3HU24719U80144203";
        String requestUrl = BASE_URL + GET_ORDER_API + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, SECRET_KEY);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();
        System.out.println("orderId: " + orderResponse.getId());
        System.out.println("validate: " + orderResponse.validate(orderId));
    }

}
