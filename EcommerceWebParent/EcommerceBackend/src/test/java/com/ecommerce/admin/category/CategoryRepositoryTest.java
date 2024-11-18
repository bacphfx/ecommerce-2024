package com.ecommerce.admin.category;

import com.ecommerce.common.entity.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Electronics");
        Category savedCategory = categoryRepository.save(category);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory(){
        Category parent = new Category(7);
        Category memory = new Category("iPhone", parent);
        categoryRepository.save(memory);

    }

    @Test
    public void testGetCategory(){
        Category category = categoryRepository.findById(2).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();
        for (Category subCate : children){
            System.out.println(subCate.getName());
        }
//        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            // Bắt đầu với các danh mục cha (parent == null)
            if (category.getParent() == null) {
                printCategoryHierarchy(category, 0); // Gọi đệ quy in tất cả từ danh mục cha
            }
        }
    }

    private void printCategoryHierarchy(Category category, int level) {
        // In ra tên danh mục với số lượng dấu "--" tương ứng với cấp độ
        for (int i = 0; i < level; i++) {
            System.out.print("--");
        }
        System.out.println(category.getName());

        // Gọi đệ quy cho các danh mục con
        Set<Category> children = category.getChildren();
        for (Category child : children) {
            printCategoryHierarchy(child, level + 1);
        }
    }

    @Test
    public void testListRootCategories(){
        List<Category> rootCategories = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category category : rootCategories){
            System.out.println(category.getName());
        }
    }

    @Test
    public void testFindByName(){
        String name = "Computers";
        Category category = categoryRepository.findByName(name);
        System.out.println(category);
    }

    @Test
    public void testFindByAlias(){
        String name = "computers";
        Category category = categoryRepository.findByAlias(name);
        System.out.println(category);
    }
}
