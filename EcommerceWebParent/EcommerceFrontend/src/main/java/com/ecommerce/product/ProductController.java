package com.ecommerce.product;

import com.ecommerce.category.CategoryService;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.exception.CategoryNotFoundException;
import com.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/c/{alias}")
    public String viewCategoryByPage(@RequestParam(name = "page", defaultValue = "1") int pageNum,
                                     @RequestParam(name = "limit", defaultValue = "10") int pageSize,
                                     @PathVariable("alias") String alias,
                                     Model model) {
        try {
            Category category = categoryService.getCategory(alias);
            List<Category> parents = categoryService.listCategoryParents(category);

            Page<Product> page = productService.listByCategory(pageNum, pageSize, category.getId());
            List<Product> products = page.getContent();

            long startCount = (pageNum - 1) * pageSize + 1;
            long endCount = startCount + pageSize - 1;
            if (endCount > page.getTotalElements()) {
                endCount = page.getTotalElements();
            }

            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);

            model.addAttribute("products", products);

            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("parents", parents);
            model.addAttribute("category", category);

            return "product/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "/error/404";
        }
    }

    @GetMapping("/p/{alias}")
    public String viewProductDetails(@PathVariable("alias") String alias,
                                     Model model){
        try {
            Product product = productService.getProduct(alias);
            List<Category> parents = categoryService.listCategoryParents(product.getCategory());

            model.addAttribute("parents", parents);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());

            return "product/product_details";
        } catch (ProductNotFoundException e) {
            return "/error/404";
        }
    }

    @GetMapping("/search")
    public String viewSearch(@RequestParam(name = "keyword") String keyword,
                             @RequestParam(name = "page", defaultValue = "1") int pageNum,
                             @RequestParam(name = "limit", defaultValue = "10") int pageSize,
                             Model model){
        Page<Product> page = productService.search(keyword,pageNum, pageSize);
        List<Product> results = page.getContent();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("keyword", keyword);
        model.addAttribute("results", results);
        model.addAttribute("pageTitle", keyword + " - Search results");
        return "product/search_results";
    }

}
