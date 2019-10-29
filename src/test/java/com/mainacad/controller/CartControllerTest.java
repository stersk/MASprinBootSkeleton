package com.mainacad.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.OrderService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.Charset;
import java.util.*;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringRunner.class)
@WebMvcTest(CartController.class)
class CartControllerTest {
  private MockMvc mockMvc;

  @MockBean
  CartService cartService;

  @MockBean
  OrderService orderService;

  @Autowired
  CartController cartController;

  public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  @BeforeEach
  void setUp() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(cartController)
            .setViewResolvers(viewResolver)
            .build();
  }

  @Test
  void getCartPage() throws Exception {
    String requestUrl = "/cart";

    // If user session parameter absent redirect to root
    this.mockMvc.perform(get(requestUrl)).andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/"))
            .andExpect(redirectedUrl("/"));

    User checkedUser = new User(1, "login1", "password1", "firstName1", "lastName");
    User userWithEmptyCart = new User(2, "login1", "password1", "firstName1", "lastName");

    Item item1 = new Item(1, "test_item", "Test item", 20000);
    Item item2 = new Item(2, "test_item_2", "Test item_2", 20000);

    List<Item> checkedItems = new ArrayList<>();
    checkedItems.add(item1);
    checkedItems.add(item2);

    Cart cart = new Cart(1, 1565024867119L, false, checkedUser);

    Order order1 = new Order(1, item1, 3, cart);
    Order order2 = new Order(2, item2, 3, cart);

    List<Order> orders = new ArrayList<>();
    orders.add(order1);
    orders.add(order2);

    List<Order> emptyOrdersList = new ArrayList<>();
    List<Item> emptyItemsList = new ArrayList<>();

    Mockito.when(cartService.getOrdersFromOpenCartByUser(checkedUser)).thenReturn(orders);
    Mockito.when(cartService.getOrdersFromOpenCartByUser(userWithEmptyCart)).thenReturn(emptyOrdersList);
    Mockito.when(cartService.findOpenCartByUser(checkedUser)).thenReturn(cart);
    Mockito.when(cartService.findOpenCartByUser(userWithEmptyCart)).thenReturn(null);
    Mockito.when(cartService.getCartSum(cart)).thenReturn(120000);

    // If session user exist and user has orders
    MvcResult result = this.mockMvc.perform(get(requestUrl).sessionAttr("user", checkedUser)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attribute("user", is(checkedUser)))
            .andExpect(forwardedUrl("/WEB-INF/jsp/cart.jsp")).andReturn();

    Map<String, Object> responseModel = Objects.requireNonNull(result.getModelAndView()).getModel();
    assertEquals(120000, (Integer) responseModel.get("cartSum"));
    assertEquals(checkedItems, (List) responseModel.get("items"));
    assertEquals(orders, (List) responseModel.get("orders"));

    // If session user exist and user has no orders
    result = this.mockMvc.perform(get(requestUrl).sessionAttr("user", userWithEmptyCart)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attribute("user", is(userWithEmptyCart)))
            .andExpect(forwardedUrl("/WEB-INF/jsp/cart.jsp")).andReturn();

    responseModel = Objects.requireNonNull(result.getModelAndView()).getModel();
    assertEquals(0, (Integer) responseModel.get("cartSum"));
    assertEquals(emptyItemsList, (List) responseModel.get("items"));
    assertEquals(emptyOrdersList, (List) responseModel.get("orders"));

    Mockito.verify(cartService, Mockito.times(1)).getOrdersFromOpenCartByUser(checkedUser);
    Mockito.verify(cartService, Mockito.times(1)).getOrdersFromOpenCartByUser(userWithEmptyCart);
    Mockito.verify(cartService, Mockito.times(1)).findOpenCartByUser(checkedUser);
    Mockito.verify(cartService, Mockito.times(1)).findOpenCartByUser(userWithEmptyCart);
    Mockito.verify(cartService, Mockito.times(1)).getCartSum(cart);
  }

  @Test
  void confirmCart() {
  }

  @Test
  void discardCart() {
  }

//  void authUserViaPost() throws Exception {
//    String requestUrl = "/auth";
//    Map<String, String> bodyData = new HashMap<>();
//
//    mockMvc.perform(post(requestUrl).contentType(APPLICATION_JSON_UTF8)
//            .content(mapDataToJson(bodyData))).andDo(print())
//            .andExpect(status().isOk());
//  }
//
//  private String mapDataToJson(Map<String, String> data) throws JsonProcessingException {
//    ObjectMapper mapper = new ObjectMapper();
//    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//    ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
//    String dataInJson = objectWriter.writeValueAsString(data);
//
//    return dataInJson;
//  }
}