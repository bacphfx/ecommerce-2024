package com.ecommerce.admin.product;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.admin.brand.BrandService;
import com.ecommerce.admin.category.CategoryService;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.security.AppUserDetails;
import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.product.Product;

import com.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;


@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listAll(@PagingAndSortingParam(listName = "products", moduleURL = "/products") PagingAndSortingHelper helper,
                          @RequestParam(name = "page", defaultValue = "1") int pageNum,
                          @RequestParam(name = "limit", defaultValue = "5") int pageSize,
                          @RequestParam(name = "categoryId", required = false) Integer categoryId,
                          Model model) {
        productService.listByPage(pageNum, pageSize, helper, categoryId);

        List<Category> categories = categoryService.listCategoriesUsedInForm();

        if (categoryId != null) model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categories);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(0);

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(name = "mainImageMultipart", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(name = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal AppUserDetails loggedUser
    ) throws IOException {
        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            if (loggedUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
                return "redirect:/products";
            }
        }
        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteImagesRemoved(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
        return "redirect:/products";
    }


    @GetMapping("/products/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID: " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImageDir = "../product-images/" + id;
            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImageDir);
            redirectAttributes.addFlashAttribute("message", "The product ID: " + id
                    + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal AppUserDetails loggedUser) {
        try {
            Product product = productService.getById(id);
            List<Brand> brands = brandService.listAll();
            Integer numberOfExtraImages = product.getImages().size();

            boolean isReadOnlyForSalesperson = false;
            if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
                if (loggedUser.hasRole("Salesperson")) {
                    isReadOnlyForSalesperson = true;
                }
            }

            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
            model.addAttribute("brands", brands);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");
            model.addAttribute("numberOfExtraImages", numberOfExtraImages);
            return "products/product_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getById(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }
}
