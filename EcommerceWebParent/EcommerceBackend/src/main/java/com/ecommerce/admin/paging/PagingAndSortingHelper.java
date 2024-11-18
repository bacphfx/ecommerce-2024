package com.ecommerce.admin.paging;

import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {
    private ModelAndViewContainer model;
    private String listName;
    private String sortBy;
    private String sortOrder;
    private String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortBy, String sortOrder, String keyword) {
        this.model = model;
        this.listName = listName;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.keyword = keyword;
    }

    public void updateModelAttribute(int pageNumber, int pageSize, Page<?> page) {
        List<?> listItems = page.getContent();

        int startCount = (pageNumber - 1) * pageSize + 1;
        int endCount = startCount + pageSize - 1;

        if (endCount > page.getTotalElements()) {
            endCount = (int) page.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);
    }

    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repository) {
        Sort sort = Sort.by(sortBy);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<?> page = null;

        if (keyword != null) {
            page = repository.findAll(keyword, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        updateModelAttribute(pageNum, pageSize, page);
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getKeyword() {
        return keyword;
    }

    public Pageable createPageable(int pageNum, int pageSize) {
        Sort sort = Sort.by(sortBy);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, pageSize, sort);
    }
}
