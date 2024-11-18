package com.ecommerce.checkout;

import com.ecommerce.Utility;
import com.ecommerce.address.AddressService;
import com.ecommerce.cart.ShoppingCartService;
import com.ecommerce.checkout.paypal.PayPalApiException;
import com.ecommerce.checkout.paypal.PayPalService;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.order.PaymentMethod;
import com.ecommerce.customer.CustomerService;
import com.ecommerce.order.OrderService;
import com.ecommerce.setting.CurrencySettingBag;
import com.ecommerce.setting.EmailSettingBag;
import com.ecommerce.setting.PaymentSettingBag;
import com.ecommerce.setting.SettingService;
import com.ecommerce.shipping.ShippingRateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {
    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private PayPalService payPalService;

    @GetMapping("/checkout")
    public String showCheckoutPage(HttpServletRequest request, Model model) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(authenticatedCustomer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress);
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", authenticatedCustomer.toString());
            shippingRate = shippingRateService.getShippingRateForCustomer(authenticatedCustomer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(authenticatedCustomer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String paypalClientId = paymentSettings.getClientID();
        String paypalSecretKey = paymentSettings.getSecretKey();

        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("paypalSecretKey", paypalSecretKey);
        model.addAttribute("customer", authenticatedCustomer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findByEmail(email).get();
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        Address defaultAddress = addressService.getDefaultAddress(authenticatedCustomer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(authenticatedCustomer);
        }
        List<CartItem> cartItems = shoppingCartService.listCartItems(authenticatedCustomer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        Order newOrder = orderService.createOrder(authenticatedCustomer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(authenticatedCustomer);

        sendConfirmationEmail(request, newOrder);

        return "checkout/order_completed";
    }

    private void sendConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        System.out.println(toAddress);
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());
        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);

        content = content.replace("[[name]]", order.getCustomer().getFullName())
                .replace("[[orderId]]", String.valueOf(order.getId()))
                .replace("[[orderTime]]", orderTime)
                .replace("[[shippingAddress]]", order.getShippingAddress())
                .replace("[[total]]", totalAmount)
                .replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);

        mailSender.send(message);
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model){
        String orderId = request.getParameter("orderId");
        String pageTitle = null;
        String message = null;

        try {
            if (payPalService.validateOrder(orderId)){
                return placeOrder(request);
            }else {
                pageTitle = "Checkout Failure";
                message = "ERROR: Transaction could not be completed because order information is invalid.";
            }
        } catch (PayPalApiException | MessagingException | UnsupportedEncodingException e) {
            message = "ERROR: Transaction failed due to error: " + e.getMessage();
        }
        model.addAttribute("pageTitle", pageTitle);
        return "";
    }
}
