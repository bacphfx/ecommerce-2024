package com.ecommerce.admin.order;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.setting.SettingService;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private SettingService settingService;

    @GetMapping("")
    public String listByPage(@PagingAndSortingParam(listName = "orders", moduleURL = "/orders")PagingAndSortingHelper helper,
                             @RequestParam(value = "page", defaultValue = "1") int pageNum,
                             @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                             HttpServletRequest request){
        orderService.listByPage(pageNum, pageSize, helper);
        loadCurrencySetting(request);
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request){
        List<Setting> currencySettings = settingService.getCurrencySetting();
        for (Setting setting : currencySettings){
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") int id,
                                  Model model, RedirectAttributes redirectAttributes,
                                  HttpServletRequest request){
        try {
            Order order = orderService.getById(id);
            model.addAttribute("order", order);
            loadCurrencySetting(request);
            return "orders/order_detail_modal";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id,
                              RedirectAttributes redirectAttributes){
        try {
            orderService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The order ID: " + id + " has been deleted");
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes,
                            HttpServletRequest request){
        try {
            Order order = orderService.getById(id);
            List<Country> countries = orderService.listAllCountries();
            model.addAttribute("countries", countries);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Edit order (ID: " + id  + ")");
            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders";
        }
    }
}
