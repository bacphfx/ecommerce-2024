package com.ecommerce.address;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/addresses")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        List<Address> addresses = addressService.listAddressBook(authenticatedCustomer);

        boolean usePrimaryAddressAsDefault = true;

        for (Address address : addresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("addresses", addresses);
        model.addAttribute("customer", authenticatedCustomer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "addresses/addresses";
    }

    @GetMapping("/addresses/new")
    public String newAddress(Model model) {
        Address newAddress = new Address();
        List<Country> countries = customerService.listAllCountries();
        model.addAttribute("address", newAddress);
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Add New Address");

        return "addresses/address_form";
    }

    @PostMapping("/addresses/save")
    public String saveAddress(Address address, RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        address.setCustomer(authenticatedCustomer);
        addressService.save(address);
        String redirectParam = request.getParameter("redirect");
        String redirectUrl = "redirect:/addresses";

        if ("checkout".equals(redirectParam)){
            redirectUrl += "?redirect=checkout";
        }
        System.out.println(redirectUrl);
        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");
        return redirectUrl;
    }

    @GetMapping("/addresses/edit/{id}")
    public String editAddress(@PathVariable("id") int id, Model model,
                              HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        Address address = addressService.get(id, authenticatedCustomer.getId());
        List<Country> countries = customerService.listAllCountries();

        model.addAttribute("address", address);
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + id + ")");

        return "addresses/address_form";
    }

    @GetMapping("/addresses/delete/{id}")
    public String deleteAddress(@PathVariable("id") int id, RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        addressService.delete(id, authenticatedCustomer.getId());
        redirectAttributes.addFlashAttribute("message", "The address has been deleted successfully.");
        return "redirect:/addresses";
    }

    @GetMapping("/addresses/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer id, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(id, customer.getId());
        String redirectParam = request.getParameter("redirect");
        String redirectUrl = "redirect:/addresses";

        if ("cart".equals(redirectParam)) {
            redirectUrl = "redirect:/cart";
        } else if ("checkout".equals(redirectParam)){
            redirectUrl = "redirect:/checkout";
        }
        return redirectUrl;
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findByEmail(email).get();
    }
}
