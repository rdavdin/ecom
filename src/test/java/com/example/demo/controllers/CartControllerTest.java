package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();

        TestUtils.injectObject(cartController, "userRepository", userRepo);
        TestUtils.injectObject(cartController, "cartRepository", cartRepo);
        TestUtils.injectObject(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void verify_addToCart_happyPath(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("new type of rose");
        item.setName("Rose type A");
        item.setPrice(BigDecimal.valueOf(2.99));

        Optional<Item> optionalItem = Optional.of(item);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(optionalItem);

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertEquals("Rose type A", responseCart.getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(2.99), responseCart.getTotal());
    }

    @Test
    public void verify_addToCart_NotFoundUser(){
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_addToCart_NotFoundItem(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_removeFromCart_happyPath(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("new type of rose");
        item.setName("Rose type A");
        item.setPrice(BigDecimal.valueOf(2.99));

        cart.addItem(item);

        Optional<Item> optionalItem = Optional.of(item);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(optionalItem);

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertEquals(new BigDecimal("0.00"), responseCart.getTotal());
    }

    @Test
    public void verify_removeFromCart_NotFoundUser(){
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_removeFromCart_NotFoundItem(){
        User user = new User();
        user.setUsername("david");
        user.setId(1L);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("david");
        r.setItemId(1L);
        r.setQuantity(1);

        when(userRepo.findByUsername("david")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
