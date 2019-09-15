package com.mainacad.controller;

import com.mainacad.App;
import com.mainacad.model.Cart;
import com.mainacad.model.Item;
import com.mainacad.model.Order;
import com.mainacad.model.User;
import com.mainacad.service.CartService;
import com.mainacad.service.OrderService;
import com.mainacad.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(App.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderControllerTest {
  @Autowired
  TestRestTemplate testRestTemplate;

  @MockBean
  OrderService orderService;

  @MockBean
  CartService cartService;

  @MockBean
  UserService userService;

  @Test
  void testGetById() throws URISyntaxException {
    Order order = new Order();
    order.setId(1);
    order.setCart(new Cart());
    order.setItem(new Item());
    order.setAmount(5);

    Mockito.when(orderService.findById(order.getId())).thenReturn(order);

    RequestEntity request = new RequestEntity(order, HttpMethod.GET, new URI("/order/get-by-id/" + order.getId().toString()));
    ResponseEntity<Order> response = testRestTemplate.exchange(request, Order.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).findById(order.getId());

    Assertions.assertEquals(order, response.getBody());
  }

  @Test
  void testGetOrdersByCartId() throws URISyntaxException {
    Cart cart = new Cart();
    cart.setId(1);

    Order order = new Order();
    order.setId(1);
    order.setCart(cart);
    order.setItem(new Item());
    order.setAmount(5);

    List<Order> ordersList = new ArrayList<>();
    ordersList.add(order);

    Mockito.when(orderService.getOrdersByCart(Mockito.any(Cart.class))).thenReturn(ordersList);
    Mockito.when(cartService.findById(cart.getId())).thenReturn(cart);

    ParameterizedTypeReference<List<Order>> listTypeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, new URI("/order/get-orders-by-card/" + cart.getId().toString()));
    //TODO Fix from ParameterizedTypeReference.forType(ordersList.getClass()) to ParameterizedTypeReference<List<Order>> typeRef = new ParameterizedTypeReference<>() {};
    // for correct deserialization of list elements everywhere in controllers test
    ResponseEntity<List<Order>> response = testRestTemplate.exchange(request, listTypeRef);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).getOrdersByCart(Mockito.any(Cart.class));
    Mockito.verify(cartService, Mockito.times(1)).findById(cart.getId());

    List<Order> responseList = response.getBody();
    Assertions.assertEquals(1, responseList.size());
    Assertions.assertEquals(order, responseList.get(0));
  }

  @Test
  void testGetOpenedOrdersByUser() throws URISyntaxException {
    User user = new User();
    user.setId(1);

    Cart cart = new Cart();
    cart.setId(1);
    cart.setUser(user);

    Order order = new Order();
    order.setId(1);
    order.setCart(cart);
    order.setItem(new Item());
    order.setAmount(5);

    List<Order> ordersList = new ArrayList<>();
    ordersList.add(order);

    Mockito.when(orderService.getOrdersByCart(cart)).thenReturn(ordersList);
    Mockito.when(cartService.findOpenCartByUser(user)).thenReturn(cart);
    Mockito.when(userService.findById(user.getId())).thenReturn(user);

    ParameterizedTypeReference<List<Order>> listTypeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, new URI("/order/get-opened-orders-by-user/" + user.getId().toString()));
    ResponseEntity<List<Order>> response = testRestTemplate.exchange(request, listTypeRef);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).getOrdersByCart(cart);
    Mockito.verify(cartService, Mockito.times(1)).findOpenCartByUser(user);
    Mockito.verify(userService, Mockito.times(1)).findById(user.getId());

    List<Order> responseList = response.getBody();
    Assertions.assertEquals(1, responseList.size());
    Assertions.assertEquals(order, responseList.get(0));
  }

  @Test
  void testAddItemToOrder() {
  }

  @Test
  void testUpdateOrderAmount() {
  }

  @Test
  void testRemoveOneFromOrder() {
  }

  @Test
  void testDelete() {
  }
}