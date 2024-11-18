package com.ecommerce.common.entity.setting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @Id
    @Column(name = "`key`", nullable = false, length = 128)
    private String key;

    @Column(nullable = false, length = 1024)
    @EqualsAndHashCode.Exclude
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    @EqualsAndHashCode.Exclude
    private SettingCategory category;

    public Setting(String key) {
        this.key = key;
    }


}
