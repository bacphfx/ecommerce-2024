package com.ecommerce.admin.setting;

import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> listAllSettings(){
        return settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSetting(){
        List<Setting> settings  = new ArrayList<>();
        List<Setting> generalSettings =  settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings =  settingRepository.findByCategory(SettingCategory.CURRENCY);
        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings){
        settingRepository.saveAll(settings);
    }

    public List<Setting> getMailServerSettings(){
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    public List<Setting> getMailTemplatesSettings(){
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }

    public List<Setting> getCurrencySetting(){
        return settingRepository.findByCategory(SettingCategory.CURRENCY);
    }

    public List<Setting> getPaymentSettings(){
        return settingRepository.findByCategory(SettingCategory.PAYMENT);
    }
}
