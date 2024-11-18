package com.ecommerce.admin.user.controllers;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.user.UserNotFoundException;
import com.ecommerce.admin.user.UserService;
import com.ecommerce.admin.user.export.UserCSVExporter;
import com.ecommerce.admin.user.export.UserExcelExporter;
import com.ecommerce.admin.user.export.UserPDFExporter;
import com.ecommerce.common.entity.Role;
import com.ecommerce.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
//
//    @GetMapping("/users")
//    public String listAllUsers(Model model) {
//        List<User> userList = userService.listAllUsers();
//        model.addAttribute("userList", userList);
//        return "users";
//    }

    @GetMapping("/users")
    public String listByPage(@PagingAndSortingParam(listName = "userList", moduleURL = "/users") PagingAndSortingHelper helper,
                             @RequestParam(name = "page", defaultValue = "1") int pageNumber,
                             @RequestParam(name = "limit", defaultValue = "4") int pageSize) {
        userService.listByPage(pageNumber, pageSize, helper);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> roles = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestPart(name = "image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");

        return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users?keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getById(id);
            List<Role> roles = userService.listRoles();
            System.out.println(user);
            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID: " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAllUsers();
        UserCSVExporter exporter = new UserCSVExporter();
        exporter.export(userList, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExecl(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAllUsers();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(userList, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAllUsers();
        UserPDFExporter exporter = new UserPDFExporter();
        exporter.export(userList, response);
    }

}
