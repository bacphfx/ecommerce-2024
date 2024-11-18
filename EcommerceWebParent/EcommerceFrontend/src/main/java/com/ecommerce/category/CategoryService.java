package com.ecommerce.category;

import com.ecommerce.common.entity.Category;
import com.ecommerce.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories(){
        List<Category> listNoChildrenCategories = new ArrayList<>();
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
        for (Category category : listEnabledCategories){
            if (category.getChildren().size() == 0){
                listNoChildrenCategories.add(category);
            }
        }
        return listNoChildrenCategories;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null){
            throw new CategoryNotFoundException("Could not find category with alias: "+ alias);
        }
        return category;
    }

    public List<Category> listCategoryParents(Category child){
        List<Category> parents = new ArrayList<>();
        Category parent = child.getParent();
        while (parent != null){
            parents.add(0, parent);
            parent = parent.getParent();
        }
        parents.add(child);
        return parents;
    }
}
