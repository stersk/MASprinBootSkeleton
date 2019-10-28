package com.mainacad.controller;

import com.mainacad.App;
import com.mainacad.entity.Item;
import com.mainacad.entity.User;
import com.mainacad.service.ItemService;
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
public class ItemRestControllerTest {

  @Autowired
  TestRestTemplate testRestTemplate;

  @MockBean
  ItemService itemService;

  @Test
  public void testCreateItem() throws URISyntaxException {
    Item item = new Item();
    item.setId(1);
    item.setItemCode("qwerty12345");
    item.setName("Kellys Spider 40 (2014)");
    item.setPrice(1450000);

    Mockito.when(itemService.save(Mockito.any(Item.class))).thenReturn(item);

    RequestEntity<Item> request = new RequestEntity<>(item, HttpMethod.POST, new URI("/item"));
    ResponseEntity<Item> response = testRestTemplate.exchange(request, Item.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(itemService, Mockito.times(1)).save(Mockito.any(Item.class));
  }

  @Test
  public void testUpdateUser() throws URISyntaxException {
    Item item = new Item();
    item.setId(1);
    item.setItemCode("qwerty12345");
    item.setName("Kellys Spider 40 (2014)");
    item.setPrice(1450000);

    Mockito.when(itemService.update(Mockito.any(Item.class))).thenReturn(item);

    RequestEntity<Item> request = new RequestEntity<>(item, HttpMethod.PUT, new URI("/item"));
    ResponseEntity<Item> response = testRestTemplate.exchange(request, Item.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(itemService, Mockito.times(1)).update(Mockito.any(Item.class));

    Assertions.assertEquals(item, response.getBody());
  }

  @Test
  public void testFindById() throws URISyntaxException {
    Item item = new Item();
    item.setId(1);
    item.setItemCode("qwerty12345");
    item.setName("Kellys Spider 40 (2014)");
    item.setPrice(1450000);

    Mockito.when(itemService.findById(item.getId())).thenReturn(item);

    RequestEntity<Item> request = new RequestEntity<>(item, HttpMethod.GET, new URI("/item/get-by-id/" + item.getId().toString()));
    ResponseEntity<Item> response = testRestTemplate.exchange(request, Item.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(itemService, Mockito.times(1)).findById(item.getId());

    Assertions.assertEquals(item, response.getBody());
  }

  @Test
  public void testGetByItemCode() throws URISyntaxException {
    List<Item> list = new ArrayList<>();
    Mockito.when(itemService.findByItemCode(Mockito.any(String.class))).thenReturn(list);

    ParameterizedTypeReference<List<Item>> typeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<Item> request = new RequestEntity<>(HttpMethod.GET, new URI("/item/get-by-code/456"));
    ResponseEntity<List<Item>> response = testRestTemplate.exchange(request, typeRef);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Mockito.verify(itemService, Mockito.times(1)).findByItemCode(Mockito.any(String.class));

    Assertions.assertEquals("ArrayList", response.getBody().getClass().getSimpleName());
  }

  @Test
  public void testGetAll() throws URISyntaxException {
    List<Item> list = new ArrayList<>();
    Mockito.when(itemService.findAll()).thenReturn(list);

    ParameterizedTypeReference<List<Item>> typeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<Item> request = new RequestEntity<>(HttpMethod.GET, new URI("/item/get-all"));
    ResponseEntity<List<Item>> response = testRestTemplate.exchange(request, typeRef);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Mockito.verify(itemService, Mockito.times(1)).findAll();

    Assertions.assertEquals("ArrayList", response.getBody().getClass().getSimpleName());
  }

  @Test
  public void testDelete() throws URISyntaxException {
    Item itemToDelete = new Item();
    itemToDelete.setId(1);
    itemToDelete.setItemCode("qwerty12345");
    itemToDelete.setName("Kellys Spider 40 (2014)");
    itemToDelete.setPrice(1450000);

    doNothing().when(itemService).delete(itemToDelete);
    Mockito.when(itemService.findById(itemToDelete.getId())).thenReturn(itemToDelete);

    RequestEntity<User> request = new RequestEntity<>(HttpMethod.DELETE, new URI("/item/" + itemToDelete.getId()));
    ResponseEntity<Void> response = testRestTemplate.exchange(request, Void.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(itemService, Mockito.times(1)).delete(itemToDelete);
  }
}