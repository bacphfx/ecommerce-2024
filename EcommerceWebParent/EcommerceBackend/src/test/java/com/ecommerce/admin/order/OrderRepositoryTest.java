package com.ecommerce.admin.order;

import com.ecommerce.common.entity.*;
import com.ecommerce.common.entity.order.*;
import com.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateOrder(){
        Customer customer = entityManager.find(Customer.class, 2);
        Product product = entityManager.find(Product.class, 2);
        Order mainOrder = new Order();
        mainOrder.setCustomer(customer);
        mainOrder.setOrderTime(new Date());
        mainOrder.setFirstName(customer.getFirstName());
        mainOrder.setLastName(customer.getLastName());
        mainOrder.setPhoneNumber(customer.getPhoneNumber());
        mainOrder.setAddressLine1(customer.getAddressLine1());
        mainOrder.setAddressLine2(customer.getAddressLine2());
        mainOrder.setCity(customer.getCity());
        mainOrder.setState(customer.getState());
        mainOrder.setCountry(customer.getCountry().getName());
        mainOrder.setZipCode(customer.getZipCode());

        mainOrder.setShippingCost(0);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(0);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = orderRepository.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateOrderTracks(){
        Integer orderId = 5;
        Order order = orderRepository.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());
        newTrack.setUpdatedTime(new Date());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setStatus(OrderStatus.PROCESSING);
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());
        processingTrack.setUpdatedTime(new Date());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = orderRepository.save(order);
        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(0);
    }
}