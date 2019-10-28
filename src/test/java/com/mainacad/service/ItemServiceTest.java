package com.mainacad.service;

import com.mainacad.ApplicationRunner;
import com.mainacad.dao.ItemDAO;
import com.mainacad.entity.Item;
import com.mainacad.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(ApplicationRunner.class)
@ActiveProfiles("test")
class ItemServiceTest {
  @Autowired
  private ItemDAO itemDAO;

  @Autowired
  private ItemService itemService;

  private static List<Item> items = new ArrayList<>();

  @BeforeEach
  void setUp() {
    Item item = new Item("test_item", "Test item", 20000);
    item = itemDAO.saveAndFlush(item);
    items.add(item);
  }

  @AfterEach
  void tearDown() {
    items.forEach(item -> itemDAO.delete(item));
    items.clear();
  }

  @Test
  void update() {
    Item checkedItem = items.get(0);
    checkedItem.setPrice(2200);

    checkedItem = itemService.update(checkedItem);
    assertNotNull(checkedItem);

    checkedItem = itemService.findById(checkedItem.getId());
    assertEquals(items.get(0), checkedItem);
    assertEquals(2200, checkedItem.getPrice());

    itemService.delete(checkedItem);
    checkedItem = itemService.update(checkedItem);
    assertNull(checkedItem);
  }

  @Test
  void findById() {
    Item checkedItem = itemService.findById(items.get(0).getId());

    assertNotNull(checkedItem);
    assertEquals(items.get(0), checkedItem);

    itemService.delete(checkedItem);

    checkedItem = itemService.findById(items.get(0).getId());
    assertNull(checkedItem);
  }
}