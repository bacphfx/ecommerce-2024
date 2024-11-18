package com.ecommerce.admin.category;

import com.ecommerce.common.entity.Category;
import com.ecommerce.common.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll(CategoryPageInfo pageInfo, int page, int limit, String sortOrder, String keyword) {
        Sort sort = sortOrder.equals("asc") ? Sort.by("name").ascending() : Sort.by("name").descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<Category> pageCategories = null;
        if (!keyword.equals("") && !keyword.isEmpty()) {
            pageCategories = categoryRepository.search(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }

        System.out.println("Total elements: " + pageCategories.getContent().size());


        List<Category> rootCategories = pageCategories.getContent();
        pageInfo.setTotalPages(pageCategories.getTotalPages());
        pageInfo.setTotalElements(pageCategories.getTotalElements());

        if (!keyword.equals("") && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult){
                category.setHasChildren(category.getChildren().size()>0);
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortOrder);
        }

    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortOrder) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            listSubHierarchicalCategories(hierarchicalCategories, rootCategory, 0, sortOrder);
        }
        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent,
                                               int level, String sortOrder) {
        String name = "";
        for (int i = 0; i < level; i++) {
            name += "--";
        }
        hierarchicalCategories.add(Category.copyFull(parent, name + parent.getName()));

        Set<Category> children = sortSubCategories(parent.getChildren(), sortOrder);
        for (Category child : children) {
            listSubHierarchicalCategories(hierarchicalCategories, child, level + 1, sortOrder);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();
        if(parent != null){
            String allParentIDs = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIDs += String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIDs);
        }
        return categoryRepository.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        List<Category> rootCategories = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category category : rootCategories) {
            listChildren(categoriesUsedInForm, category, 0);
        }
        return categoriesUsedInForm;
    }

    private void listChildren(List<Category> categoriesUsedInForm, Category category, int level) {
        String name = "";
        for (int i = 0; i < level; i++) {
            name += "--";
        }
        categoriesUsedInForm.add(Category.copyIdAndName(category.getId(), name + category.getName()));

        Set<Category> children = sortSubCategories(category.getChildren());
        for (Category child : children) {
            listChildren(categoriesUsedInForm, child, level + 1);
        }
    }

    public Category getById(int id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CategoryNotFoundException("Could not find any category with ID: " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        Category categoryByName = categoryRepository.findByName(name);
        Category categoryByAlias = categoryRepository.findByAlias(alias);

        boolean isNew = (id == null || id == 0);

        if (categoryByName != null && (isNew || !categoryByName.getId().equals(id))) {
            return "DuplicateName";
        }

        if (categoryByAlias != null && (isNew || !categoryByAlias.getId().equals(id))) {
            return "DuplicateAlias";
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortOrder) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortOrder.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    @Transactional
    public void updateEnabledStatus(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    public void deleteById(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
