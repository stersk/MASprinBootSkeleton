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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
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
  void addItemToCart() throws Exception {
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
  void removeFromOpenCart() {
  }

  @Test
  void updateItemAmountInOrder() {
  }

  private String mapDataToJson(Map<String, String> data) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter objectWriter = mapper.writer();
    String dataInJson = objectWriter.writeValueAsString(data);

    return dataInJson;
  }
}