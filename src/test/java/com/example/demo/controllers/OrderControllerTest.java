package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class OrderControllerTest {
    private OrderController orderController = mock(OrderController.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);


    @Before
    public void setUp(){
        orderController = new OrderController();

        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void verify_submitOrder_happyPath(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("new type of rose");
        item.setName("Rose type A");
        item.setPrice(BigDecimal.valueOf(2.02));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("old type of rose");
        item2.setName("Rose type B");
        item2.setPrice(BigDecimal.valueOf(1.01));

        cart.addItem(item);
        cart.addItem(item2);

        when(userRepository.findByUsername("david")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("david");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(2, userOrder.getItems().size());
        assertEquals(new BigDecimal("3.03"), userOrder.getTotal());
    }

    @Test
    public void verify_summitOrder_userNotFound(){
        when(userRepository.findByUsername("david")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("david");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_historyOrdersOfUser_happyPath(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("new type of rose");
        item.setName("Rose type A");
        item.setPrice(BigDecimal.valueOf(2.02));

        List<Item> list = new ArrayList<Item>();
        list.add(item);

        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setItems(list);
        userOrder.setTotal(BigDecimal.valueOf(2.02));

        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(userOrder);

        when(userRepository.findByUsername("david")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("david");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final List<UserOrder> responseUserOrders = response.getBody();
        assertNotNull(responseUserOrders);
        assertEquals(1, responseUserOrders.size());
        assertEquals(new BigDecimal("2.02"), responseUserOrders.get(0).getTotal());
    }

    @Test
    public void verify_historyOrdersOfUser_userNotFound(){
        when(userRepository.findByUsername("david")).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("david");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
