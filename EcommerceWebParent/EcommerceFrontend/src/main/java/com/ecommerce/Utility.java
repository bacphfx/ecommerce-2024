package com.ecommerce;

import com.ecommerce.security.oauth.CustomerOAuth2User;
import com.ecommerce.setting.CurrencySettingBag;
import com.ecommerce.setting.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {
    public static String getSiteUrl(HttpServletRequest request){
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag emailSettingBag){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailSettingBag.getHost());
        mailSender.setPort(emailSettingBag.getPort());
        mailSender.setUsername(emailSettingBag.getUsername());
        mailSender.setPassword(emailSettingBag.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", emailSettingBag.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", emailSettingBag.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request){
        Object principal = request.getUserPrincipal();
        if (principal == null) return null;
        String customerEmail = null;
        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User customerOAuth2User= (CustomerOAuth2User) token.getPrincipal();
            customerEmail = customerOAuth2User.getEmail();
        }
        return customerEmail;
    }

    public static String formatCurrency(float amount, CurrencySettingBag currencySettingBag){
        String symbol = currencySettingBag.getSymbol();
        String symbolPosition = currencySettingBag.getSymbolPosition();
        String decimalPointType = currencySettingBag.getDecimalPointType();
        String thousandPointType = currencySettingBag.getThousandPointType();
        int decimalDigits = currencySettingBag.getDecimalDigits();

        String pattern = symbolPosition.equals("Before price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0){
            pattern += ".";
            for (int count = 1; count <= decimalDigits; count ++) pattern += "#";
        }

        pattern += symbolPosition.equals("After price") ? symbol : "";

        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
        return formatter.format(amount);
    }
}
