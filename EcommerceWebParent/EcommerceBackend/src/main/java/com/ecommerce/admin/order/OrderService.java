package com.ecommerce.admin.order;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.setting.country.CountryRepository;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CountryRepository countryRepository;

    public void listByPage(int pageNum, int pageSize, PagingAndSortingHelper helper){
        String sortBy = helper.getSortBy();
        String sortOrder = helper.getSortOrder();
        String keyword = helper.getKeyword();
        Sort sort = null;
        if (sortBy.equals("destination")){
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortBy);
        }
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<Order> page = null;
        if (keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        helper.updateModelAttribute(pageNum, pageSize, page);
    }

    public Order getById(Integer id) throws OrderNotFoundException {
        Optional<Order> optional = orderRepository.findById(id);
        if (optional.isEmpty()){
            throw new OrderNotFoundException("Could not find any order with ID: " + id);
        }
        return optional.get();
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Long count = orderRepository.countById(id);
        if (count ==null || count == 0){
            throw new OrderNotFoundException("Could not find any order with ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Country> listAllCountries(){
        return countryRepository.findAllByOrderByNameAsc();
    }
}
