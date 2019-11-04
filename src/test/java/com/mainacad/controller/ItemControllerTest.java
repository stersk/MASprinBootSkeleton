package com.mainacad.controller;

import com.mainacad.entity.Item;
import com.mainacad.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {
  private MockMvc mockMvc;

  @MockBean
  private ItemService itemService;

  @Autowired
  private ItemController itemController;

  @BeforeEach
  void setUp() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setViewResolvers(viewResolver)
            .build();
  }

  @Test
  void getItemsPage() throws Exception {
    Item item1 = new Item(1, "1", "firstItem", 100);
    Item item2 = new Item(2, "2", "secondItem", 100);
    List<Item> itemsList = new ArrayList<>();
    itemsList.add(item1);
    itemsList.add(item2);

    Mockito.when(itemService.findAll()).thenReturn(itemsList);

    this.mockMvc.perform(get("/items")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("items"))
            .andExpect(forwardedUrl("/WEB-INF/jsp/items.jsp"))
            .andExpect(model().attribute("items", itemsList));

    Mockito.verify(itemService, Mockito.times(1)).findAll();
  }
}