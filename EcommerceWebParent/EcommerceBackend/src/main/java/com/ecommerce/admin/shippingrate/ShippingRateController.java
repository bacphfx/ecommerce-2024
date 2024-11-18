package com.ecommerce.admin.shippingrate;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Role;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shipping_rates")
public class ShippingRateController {
    @Autowired
    private ShippingRateService shippingRateService;

    @GetMapping("")
    public String listByPage(@PagingAndSortingParam(listName = "shippingRates",
            moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
                             @RequestParam(value = "page", defaultValue = "1") int pageNum,
                             @RequestParam(value = "limit", defaultValue = "10") int pageSize) {
            shippingRateService.listByPage(pageNum, pageSize, helper);
            return "shipping_rates/shipping_rates";
    }

    @GetMapping("/new")
    public String newRate(Model model) {
        List<Country> countries = shippingRateService.listAllCountries();
        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setCodSupported(true);
        model.addAttribute("rate", shippingRate);
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Create New Shipping Rate");
        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/save")
    public String saveRate(ShippingRate shippingRate, RedirectAttributes redirectAttributes){
        try {
            shippingRateService.save(shippingRate);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully.");
        } catch (ShippingRateAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates";
    }

    @GetMapping("/edit/{id}")
    public String editRate(@PathVariable("id") int id, Model model,
                           RedirectAttributes redirectAttributes){
        try {
            ShippingRate shippingRate = shippingRateService.getById(id);
            List<Country> countries = shippingRateService.listAllCountries();

            model.addAttribute("countries", countries);
            model.addAttribute("rate", shippingRate);
            model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");
            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/shipping_rates";
        }
    }

    @GetMapping("/{id}/cod_supported/{enabled}")
    public String updateCodSupported(@PathVariable("id") int id,
                                     @PathVariable("enabled") boolean enabled,
                                     RedirectAttributes redirectAttributes){
        try {
            shippingRateService.updateCODSupported(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            String message = "The shipping rate ID: " + id + " has been " + status + " COD supported";
            redirectAttributes.addFlashAttribute("message", message);
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates";
    }

    @GetMapping("/delete/{id}")
    public String deleteRate(@PathVariable("id") int id,
                             RedirectAttributes redirectAttributes){
        try {
            shippingRateService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been deleted successfully.");
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates";
    }
}
