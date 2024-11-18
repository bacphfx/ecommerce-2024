package com.ecommerce.checkout.paypal;

import com.ecommerce.setting.PaymentSettingBag;
import com.ecommerce.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class PayPalService {
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Autowired
    private SettingService settingService;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);
        return orderResponse.validate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOKResponse(statusCode);
        }
        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseUrl = paymentSettings.getURL();
        String requestUrl = baseUrl + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(paymentSettings.getClientID(), paymentSettings.getSecretKey());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
        return response;
    }

    private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = null;
        switch (statusCode) {
            case NOT_FOUND:
                message = "Order ID not found";
            case BAD_REQUEST:
                message = "Bad Request to PayPal checkout API";
            case INTERNAL_SERVER_ERROR:
                message = "Internal Server Error";
            default:
                message = "PayPal return non-OK status code";
        }
        throw new PayPalApiException(message);
    }
}
