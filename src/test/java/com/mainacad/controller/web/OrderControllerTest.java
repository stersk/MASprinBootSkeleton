package com.mainacad.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mainacad.controller.web.OrderController;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.ItemService;
import com.mainacad.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
@ActiveProfiles("web")
class OrderControllerTest {
  private MockMvc mockMvc;

  @Autowired
  private OrderController orderController;

  @MockBean
  ItemService itemService;

  @MockBean
  OrderService orderService;

  @MockBean
  CartService cartService;

  @BeforeEach
  void setUp() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(orderController)
            .setViewResolvers(viewResolver)
            .build();
  }

  @Test
  void testAddItemToCart() throws Exception {
    String requestUrl = "/order/addItemToCart";

    // If user session parameter absent redirect to root
    this.mockMvc.perform(post(requestUrl).contentType(MediaType.APPLICATION_FORM_URLENCODED)).andDo(print())
            .andExpect(status().isBadRequest());

    User checkedUser = new User(1, "login", "password", "firstName", "secondName");
    User checkedUser1 = new User(2, "login1", "password1", "firstName1", "secondName1");

    Item checkedItem = new Item(1, "123", "name", 1000);

    Cart checkedCart = new Cart(1, 1565024867119L, false, checkedUser);

    Order checkedOrder = new Order(1, checkedItem, 1, checkedCart);

    Mockito.when(itemService.findById(checkedItem.getId())).thenReturn(checkedItem);
    Mockito.when(itemService.findById(2)).thenReturn(null);
    Mockito.when(orderService.addItemToOrder(checkedItem, checkedUser)).thenReturn(checkedOrder);
    Mockito.when(orderService.addItemToOrder(checkedItem, checkedUser1)).thenReturn(null);

    mockMvc.perform(post(requestUrl)
            .sessionAttr("user", checkedUser)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)).andDo(print())
            .andExpect(status().isBadRequest());

    mockMvc.perform(post(requestUrl)
            .sessionAttr("user", checkedUser)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("itemId", "2")).andDo(print())
            .andExpect(status().isNotFound());

    Mockito.verify(itemService, Mockito.times(1)).findById(2);

    MvcResult result = mockMvc.perform(post(requestUrl)
            .sessionAttr("user", checkedUser)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("itemId", "1")).andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    ObjectMapper objectMapper = new ObjectMapper();
    Item item = objectMapper.readValue(result.getResponse().getContentAsString(), Item.class);
    assertEquals(checkedItem, item);

    Mockito.verify(itemService, Mockito.times(1)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).addItemToOrder(checkedItem, checkedUser);

    mockMvc.perform(post(requestUrl)
            .sessionAttr("user", checkedUser1)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("itemId", "1")).andDo(print())
            .andExpect(status().is5xxServerError())
            .andReturn();

    Mockito.verify(itemService, Mockito.times(2)).findById(1);
    Mockito.verify(orderService, Mockito.times(1)).addItemToOrder(checkedItem, checkedUser1);
  }

  @Test
  void testRemoveFromOpenCart() throws Exception {
    String requestUrl = "/order/removeFromOpenCart";

    User checkedUser = new User(1, "login", "password", "firstName", "secondName");
    Item checkedItem = new Item(1, "123", "name", 1000);
    Cart checkedCart = new Cart(1, 1565024867119L, false, checkedUser);
    Order checkedOrder1 = new Order(1, checkedItem, 1, checkedCart);
    Order checkedOrder2 = new Order(2, checkedItem, 1, checkedCart);

    Mockito.when(orderService.findById(checkedOrder1.getId())).thenReturn(checkedOrder1);
    Mockito.when(cartService.getCartSum(checkedOrder1.getCart())).thenReturn(1000);

    MvcResult result = mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("orderId", "1")).andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    ObjectMapper objectMapper = new ObjectMapper();
    Map responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("cartSum"));
    assertEquals(1000, responseData.get("cartSum"));

    Mockito.verify(cartService, Mockito.times(1)).getCartSum(checkedOrder1.getCart());
    Mockito.verify(orderService, Mockito.times(1)).findById(checkedOrder1.getId());
    Mockito.verify(orderService, Mockito.times(1)).deleteOrder(checkedOrder1);
  }

  @Test
  void testUpdateItemAmountInOrder() throws Exception {
    String requestUrl = "/order/updateItemAmountInOrder";

    User checkedUser = new User(1, "login", "password", "firstName", "secondName");
    Item checkedItem = new Item(1, "123", "name", 1000);
    Cart checkedCart = new Cart(1, 1565024867119L, false, checkedUser);
    Order checkedOrder1 = new Order(1, checkedItem, 1, checkedCart);
    Order checkedOrder2 = new Order(2, checkedItem, 1, checkedCart);

    // update items amount (result amount not 0)
    Mockito.when(orderService.findById(checkedOrder1.getId())).thenReturn(checkedOrder1);
    Mockito.when(cartService.getCartSum(checkedOrder1.getCart())).thenReturn(1000);
    Mockito.when(orderService.updateItemAmountInOrder(checkedOrder1, 1)).thenReturn(checkedOrder1);

    MvcResult result = mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("orderId", "1")
            .param("amount", "1")).andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    ObjectMapper objectMapper = new ObjectMapper();
    Map responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("cartSum"));
    assertEquals(1000, responseData.get("cartSum"));

    Mockito.verify(cartService, Mockito.times(1)).getCartSum(checkedOrder1.getCart());
    Mockito.verify(orderService, Mockito.times(1)).findById(checkedOrder1.getId());
    Mockito.verify(orderService, Mockito.times(1)).updateItemAmountInOrder(checkedOrder1, 1);
    Mockito.verify(orderService, Mockito.never()).deleteOrder(checkedOrder1);

    // update items amount (result amount is 0)
    Mockito.reset(orderService, cartService);

    Mockito.when(orderService.findById(checkedOrder1.getId())).thenReturn(checkedOrder1);
    Mockito.when(cartService.getCartSum(checkedOrder1.getCart())).thenReturn(1000);
    Mockito.when(orderService.updateItemAmountInOrder(checkedOrder1, 1)).thenReturn(checkedOrder1);

    result = mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("orderId", "1")
            .param("amount", "0")).andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("cartSum"));
    assertEquals(1000, responseData.get("cartSum"));

    Mockito.verify(cartService, Mockito.times(1)).getCartSum(checkedOrder1.getCart());
    Mockito.verify(orderService, Mockito.times(1)).findById(checkedOrder1.getId());
    Mockito.verify(orderService, Mockito.never()).updateItemAmountInOrder(checkedOrder1, 0);
    Mockito.verify(orderService, Mockito.times(1)).deleteOrder(checkedOrder1);

    // order not found
    Mockito.reset(orderService, cartService);

    Mockito.when(orderService.findById(checkedOrder1.getId())).thenReturn(checkedOrder1);
    Mockito.when(cartService.getCartSum(checkedOrder1.getCart())).thenReturn(1000);
    Mockito.when(orderService.updateItemAmountInOrder(checkedOrder1, 1)).thenReturn(null);

    result = mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("orderId", "1")
            .param("amount", "1")).andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("cartSum"));
    assertEquals(0, responseData.get("cartSum"));

    Mockito.verify(cartService, Mockito.never()).getCartSum(checkedOrder1.getCart());
    Mockito.verify(orderService, Mockito.times(1)).findById(checkedOrder1.getId());
    Mockito.verify(orderService, Mockito.times(1)).updateItemAmountInOrder(checkedOrder1, 1);
    Mockito.verify(orderService, Mockito.never()).deleteOrder(checkedOrder1);
  }
}