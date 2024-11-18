package com.ecommerce.setting;

import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s From Setting s WHERE s.category = ?1 OR s.category = ?2")
    public List<Setting> findByTwoCategories(SettingCategory cat1, SettingCategory cat2);

    public Setting findByKey(String key);
}
