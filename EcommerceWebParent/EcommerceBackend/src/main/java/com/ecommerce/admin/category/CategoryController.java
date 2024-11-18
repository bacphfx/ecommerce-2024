package com.ecommerce.admin.category;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.admin.category.export.CategoryCSVExporter;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listByPage(@RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "limit", defaultValue = "4") int limit,
                             @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder,
                             @RequestParam(name = "keyword", defaultValue = "") String keyword,
                             Model model) {
        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

        CategoryPageInfo pageInfo = new CategoryPageInfo();

        List<Category> categories = categoryService.listAll(pageInfo, page, limit, sortOrder, keyword);

        int startCount = (page - 1) * limit + 1;
        int endCount = startCount + limit - 1;

        if (endCount > pageInfo.getTotalElements()) {
            endCount = (int) pageInfo.getTotalElements();
        }

        model.addAttribute("categories", categories);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("sortBy", "name");
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/categories");
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> categories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Create new category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                               @RequestParam("imageUpload") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);
            String uploadDir = "../category-image/" + savedCategory.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }
        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Integer id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getById(id);
            List<Category> categories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnableStatus(@PathVariable("id") Integer id,
                                             @PathVariable("status") boolean enabled,
                                             RedirectAttributes redirectAttributes) {
        categoryService.updateEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID: " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            String categoryDir = "../category-image/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category with ID " + id +
                    " has been deleted successfully");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categoryList = categoryService.listCategoriesUsedInForm();
        CategoryCSVExporter exporter = new CategoryCSVExporter();
        exporter.export(categoryList, response);
    }
}
