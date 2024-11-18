package com.ecommerce.admin.brand;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.admin.category.CategoryService;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands")
    public String listAll(@PagingAndSortingParam(listName = "brands", moduleURL = "/brands") PagingAndSortingHelper helper,
                          @RequestParam(name = "page", defaultValue = "1") int pageNum,
                          @RequestParam(name = "limit", defaultValue = "10") int pageSize) {
        brandService.listByPage(pageNum, pageSize, helper);

        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        List<Category> categories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("categories", categories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Create New Brand");
        return "/brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
                            @RequestParam("imageUpload") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "../brand-logo/" + savedBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            brandService.save(brand);
        }
        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully!");
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable Integer id, Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.getById(id);
            List<Category> categories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

            return "brands/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable Integer id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);
            String brandDir = "../brand-logo/" + id;
            FileUploadUtil.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message", "The brand with ID " + id +
                    " has been deleted successfully");
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";
    }
}
