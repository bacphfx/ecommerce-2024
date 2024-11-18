package com.ecommerce.setting;

import com.ecommerce.common.entity.Currency;
import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private CurrencyRepository currencyRepository;


    public List<Setting> getGeneralSettings(){
       return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

//    public List<Setting> getEmailSettings(){
//        return settingRepository.findByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
//    }

    public EmailSettingBag getEmailSettings(){
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));

        return new EmailSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings){
        settingRepository.saveAll(settings);
    }

    public CurrencySettingBag getCurrencySettings(){
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(settings);
    }

    public PaymentSettingBag getPaymentSettings(){
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(settings);
    }

    public String getCurrencyCode(){
       Setting setting = settingRepository.findByKey("CURRENCY_ID");
       Integer currencyId = Integer.parseInt(setting.getValue());
        Optional<Currency> optional = currencyRepository.findById(currencyId);
        return optional.get().getCode();
    }
}
