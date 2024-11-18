package com.ecommerce.admin.shippingrate;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.product.ProductRepository;
import com.ecommerce.admin.setting.country.CountryRepository;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ShippingRateService {
    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ProductRepository productRepository;

    public void listByPage(int pageNum, int pageSize, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, pageSize, shippingRateRepository);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
        ShippingRate rateInDB = shippingRateRepository.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
        boolean foundExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.getId().equals(rateInForm.getId());
        if (foundExistingRateInNewMode || foundExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }
        shippingRateRepository.save(rateInForm);
    }

    public ShippingRate getById(Integer id) throws ShippingRateNotFoundException {
        try {
            return shippingRateRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID: " + id);
        }
    }

    public void updateCODSupported(Integer id, boolean enabled) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0){
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID: " + id);
        }
        shippingRateRepository.updateCODSupported(id, enabled);
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0){
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID: " + id);
        }
        shippingRateRepository.deleteById(id);
    }

    public float calculateShippingCost(int productId, int countryId, String state) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);
        if (shippingRate == null){
            throw new ShippingRateNotFoundException("No shipping rate found for the given destination," +
                    "you have to enter shipping cost manually");
        }
        Product product = productRepository.findById(productId).get();
        return product.getWeight() * shippingRate.getRate();
    }
}
