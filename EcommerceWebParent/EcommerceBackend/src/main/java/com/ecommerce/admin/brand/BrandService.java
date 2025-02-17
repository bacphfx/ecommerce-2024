package com.ecommerce.admin.brand;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listAll(){
        return brandRepository.findAll();
    }

    public void listByPage(int pageNum, int pageSize, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, pageSize, brandRepository);
    }

    public Brand save(Brand brand){
        return brandRepository.save(brand);
    }

    public Brand getById(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new BrandNotFoundException("Could not find any brand with ID: " + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0){
            throw new BrandNotFoundException("Could not find any brand with ID: " + id);
        }
        brandRepository.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        Brand brandByName = brandRepository.findByName(name);

        if (brandByName == null) {
            return "OK";
        }

        boolean isCreatingNew = (id == null || id == 0);

        if (isCreatingNew || !brandByName.getId().equals(id)) {
            return "Duplicate";
        }

        return "OK";
    }
}
