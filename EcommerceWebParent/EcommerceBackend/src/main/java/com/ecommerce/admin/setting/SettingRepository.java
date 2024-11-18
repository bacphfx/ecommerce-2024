package com.ecommerce.admin.setting;

import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);
}
