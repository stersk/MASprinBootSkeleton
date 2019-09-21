package com.mainacad.controller;

import com.mainacad.App;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;

@SpringJUnitConfig(App.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CartControllerTest {
  @Autowired
  TestRestTemplate testRestTemplate;

  @MockBean
  CartService cartService;

  @MockBean
  OrderService orderService;

  @Test
  void testGetById() throws URISyntaxException {
    Cart cart = new Cart();
    cart.setId(1);
    cart.setClosed(false);
    cart.setCreationTime(1565024867119L);
    cart.setUser(new User());

    Mockito.when(cartService.findById(cart.getId())).thenReturn(cart);

    RequestEntity<Cart> request = new RequestEntity<>(cart, HttpMethod.GET, new URI("/cart/get-by-id/" + cart.getId().toString()));
    ResponseEntity<Cart> response = testRestTemplate.exchange(request, Cart.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(cartService, Mockito.times(1)).findById(cart.getId());

    Assertions.assertEquals(cart, response.getBody());
  }

  @Test
  void testGetSumById() throws URISyntaxException {
    Cart cart = new Cart();
    cart.setId(1);
    cart.setClosed(false);
    cart.setCreationTime(1565024867119L);
    cart.setUser(new User());

    Integer sum = Mockito.anyInt();

    Mockito.when(cartService.getCartSum(cart)).thenReturn(sum);
    Mockito.when(cartService.findById(cart.getId())).thenReturn(cart);

    RequestEntity<Cart> request = new RequestEntity<>(cart, HttpMethod.GET, new URI("/cart/get-sum-by-id/" + cart.getId().toString()));
    ResponseEntity<Integer> response = testRestTemplate.exchange(request, Integer.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(cartService, Mockito.times(1)).getCartSum(cart);
    Mockito.verify(cartService, Mockito.times(1)).findById(cart.getId());

    Assertions.assertEquals(sum, response.getBody());
  }

  @Test
  void testCloseCart() throws URISyntaxException {
    Cart cart = new Cart();
    cart.setId(1);
    cart.setClosed(false);
    cart.setCreationTime(1565024867119L);
    cart.setUser(new User());

    Mockito.when(cartService.close(cart)).thenReturn(cart);
    Mockito.when(cartService.findById(cart.getId())).thenReturn(cart);

    RequestEntity<Cart> request = new RequestEntity<>(cart, HttpMethod.PUT, new URI("/cart/close/" + cart.getId().toString()));
    ResponseEntity<Cart> response = testRestTemplate.exchange(request, Cart.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(cartService, Mockito.times(1)).close(cart);
    Mockito.verify(cartService, Mockito.times(1)).findById(cart.getId());

    Assertions.assertEquals(cart, response.getBody());
  }

  @Test
  void testCreateCart() throws URISyntaxException {
    User user = new User();

    Cart cart = new Cart();
    cart.setId(1);
    cart.setClosed(false);
    cart.setCreationTime(1565024867119L);
    cart.setUser(user);

    Mockito.when(cartService.createCartForUser(user)).thenReturn(cart);

    RequestEntity<User> request = new RequestEntity<>(user, HttpMethod.POST, new  URI("/cart"));
    ResponseEntity<Cart> response = testRestTemplate.exchange(request, Cart.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    Mockito.verify(cartService, Mockito.times(1)).createCartForUser(user);

    Cart checkedCart = response.getBody();
    Assertions.assertEquals(cart, checkedCart);
    Assertions.assertEquals(user, checkedCart.getUser());
  }

  @Test
  void testDelete() throws URISyntaxException {
    User user = new User();

    Cart cart = new Cart();
    cart.setId(1);
    cart.setClosed(false);
    cart.setCreationTime(1565024867119L);
    cart.setUser(user);

    Order order = new Order();
    order.setId(1);
    order.setCart(cart);
    order.setItem(new Item());
    order.setAmount(2);

    List<Order> ordersToDelete = new ArrayList<>();
    ordersToDelete.add(order);

    doNothing().when(cartService).deleteCart(cart);
    Mockito.when(cartService.findById(cart.getId())).thenReturn(cart);
    Mockito.when(orderService.getOrdersByCart(cart)).thenReturn(ordersToDelete);

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.DELETE, new URI("/cart/" + cart.getId()));
    ResponseEntity<Void> response = testRestTemplate.exchange(request, Void.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).getOrdersByCart(cart);
    Mockito.verify(cartService, Mockito.times(1)).findById(cart.getId());
    Mockito.verify(cartService, Mockito.times(1)).deleteCart(cart);
  }
}