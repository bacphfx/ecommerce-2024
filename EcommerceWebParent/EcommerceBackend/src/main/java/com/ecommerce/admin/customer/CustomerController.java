package com.ecommerce.admin.customer;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public String listAll(@PagingAndSortingParam(listName = "customers", moduleURL = "/customers")PagingAndSortingHelper helper,
                          @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
                          @RequestParam(name = "limit", defaultValue = "10") Integer pageSize){
        customerService.listByPage(pageNum, pageSize, helper);
        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The customer ID: " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Integer id, Model model,
                               RedirectAttributes redirectAttributes){
        try {
            Customer customer = customerService.getById(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("customer", customer);
            model.addAttribute("countries", countries);
            model.addAttribute("pageTitle", "Edit customer (ID: " + id + ")");

            return "customers/customer_form";
        } catch (CustomerNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes redirectAttributes){
        customerService.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "The customer ID: " + customer.getId() + " has been updated successfully!");
        return "redirect:/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes){
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("message", "The customer ID: " + id + " has been deleted successfully!");
        }catch (CustomerNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomerDetail(@PathVariable Integer id, Model model,
                                     RedirectAttributes redirectAttributes){
        try {
            Customer customer = customerService.getById(id);

            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }
    }
}
