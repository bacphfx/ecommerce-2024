package com.ecommerce.admin.user;

import com.ecommerce.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){
        Role adminRole = new Role("admin", "manage everything");
        Role savedRole = roleRepository.save(adminRole);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles(){
        Role salespersonRole = new Role("Salesperson", "manage product price, " +
                "customers, shipping, orders and sales report");

        Role editorRole = new Role("Editor", "manage categories, " +
                "brands, products, articles and menus");

        Role shipperRole = new Role("Shipper", "view products, " +
                "view orders and update order status");

        Role assistantRole = new Role("Assistant", "manage questions and reviews");

        roleRepository.saveAll(List.of(salespersonRole, editorRole, shipperRole, assistantRole));
    }
}
