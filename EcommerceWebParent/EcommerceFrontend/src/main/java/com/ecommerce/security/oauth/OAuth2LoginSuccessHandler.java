package com.ecommerce.security.oauth;

import com.ecommerce.common.entity.AuthenticationType;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    @Lazy
    private CustomerService customerService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oAuth2User.getClientName();
        AuthenticationType authenticationType = getAuthenticationType(clientName);
        Optional<Customer> optional = customerService.findByEmail(email);
        if (optional.isEmpty()){
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        }else {
            Customer customer = optional.get();
            oAuth2User.setFullName(customer.getFullName());
            customerService.updateAuthentication(customer, authenticationType);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName){
        if (clientName.equals("Google")){
            return AuthenticationType.GOOGLE;
        } else if (clientName.equals("Facebook")){
            return AuthenticationType.FACEBOOK;
        }
        return AuthenticationType.DATABASE;
    }
}
