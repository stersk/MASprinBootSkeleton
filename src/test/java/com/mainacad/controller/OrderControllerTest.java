package com.mainacad.controller;

import com.mainacad.App;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.ItemService;
import com.mainacad.service.OrderService;
import com.mainacad.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.mockito.Mockito.doNothing;

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

  @MockBean
  ItemService itemService;

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
  void testAddItemToOrder() throws URISyntaxException {
    Item item = new Item("test_item", "Test item", 20000);
    User user = new User("login", "password", "surName", "name");
    Order order = new Order(item, 1, new Cart());

    user.setId(1);
    item.setId(1);
    order.setId(1);

    Mockito.when(orderService.addItemToOrder(item, user)).thenReturn(order);
    Mockito.when(userService.findById(1)).thenReturn(user);
    Mockito.when(itemService.findById(1)).thenReturn(item);

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.PUT, new URI("/order/add-item-to-order/"+ item.getId().toString() + "/" + user.getId().toString()));
    ResponseEntity<Order> response = testRestTemplate.exchange(request, Order.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).addItemToOrder(item, user);
    Mockito.verify(userService, Mockito.times(1)).findById(1);
    Mockito.verify(itemService, Mockito.times(1)).findById(1);

    Assertions.assertEquals(order, response.getBody());
  }

  @Test
  void testUpdateOrderAmount() throws URISyntaxException {
    Order order = new Order(new Item(), 1, new Cart());
    order.setId(1);

    Mockito.when(orderService.findById(1)).thenReturn(order);
    Mockito.when(orderService.updateItemAmountInOrder(order, 2)).thenReturn(order);

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.PUT, new URI("/order/update-order-amount/"+ order.getId().toString() + "/2"));
    ResponseEntity<Order> response = testRestTemplate.exchange(request, Order.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).updateItemAmountInOrder(order, 2);
    Mockito.verify(orderService, Mockito.times(0)).deleteOrder(order);

    Assertions.assertEquals(order, response.getBody());

    Mockito.reset(orderService);
    Mockito.when(orderService.findById(1)).thenReturn(order);
    Mockito.when(orderService.updateItemAmountInOrder(Mockito.any(Order.class), Mockito.anyInt())).thenReturn(order);

    request = new RequestEntity<>(HttpMethod.PUT, new URI("/order/update-order-amount/"+ order.getId().toString() + "/0"));
    response = testRestTemplate.exchange(request, Order.class);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(0)).updateItemAmountInOrder(order, 2);
    Mockito.verify(orderService, Mockito.times(1)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).deleteOrder(order);

    Assertions.assertEquals(null, response.getBody());
  }

  @Test
  void testRemoveOneFromOrder() throws URISyntaxException {
    Order order = new Order(new Item(), 2, new Cart());
    order.setId(1);

    Mockito.when(orderService.findById(1)).thenReturn(order);
    Mockito.when(orderService.updateItemAmountInOrder(order, 1)).thenReturn(order);

    RequestEntity<Void> request = new RequestEntity<>(HttpMethod.PUT, new URI("/order/remove-one-from-order/"+ order.getId().toString()));
    ResponseEntity<Integer> response = testRestTemplate.exchange(request, Integer.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).updateItemAmountInOrder(order, 1);
    Mockito.verify(orderService, Mockito.times(0)).deleteOrder(order);

    Assertions.assertEquals(1, response.getBody());

    Mockito.reset(orderService);
    Mockito.when(orderService.findById(1)).thenReturn(order);

    request = new RequestEntity<>(HttpMethod.PUT, new URI("/order/remove-one-from-order/"+ order.getId().toString()));
    response = testRestTemplate.exchange(request, Integer.class);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(0)).updateItemAmountInOrder(order, 1);
    Mockito.verify(orderService, Mockito.times(1)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).deleteOrder(order);

    Assertions.assertEquals(0, response.getBody());
  }

  @Test
  void testDelete() throws URISyntaxException {
    Order order = new Order();
    order.setId(1);

    doNothing().when(orderService).deleteOrder(order);
    Mockito.when(orderService.findById(1)).thenReturn(order);
    Mockito.when(orderService.findById(2)).thenReturn(null);

    RequestEntity<User> request = new RequestEntity<>(HttpMethod.DELETE, new URI("/order/1"));
    ResponseEntity<Void> response = testRestTemplate.exchange(request, Void.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(orderService, Mockito.times(1)).deleteOrder(order);
    Mockito.verify(orderService, Mockito.times(1)).findById(1);

    request = new RequestEntity<>(HttpMethod.DELETE, new URI("/order/2"));
    response = testRestTemplate.exchange(request, Void.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    Mockito.verify(orderService, Mockito.times(1)).findById(2);
  }
}