package com.mainacad.controller;

import com.mainacad.entity.Item;
import com.mainacad.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemRestController {
  @Autowired
  private ItemService itemService;

  @PostMapping()
  public ResponseEntity<Item> createUser(@RequestBody Item item) {
    Item savedItem = itemService.save(item);
    if (savedItem != null) {
      return new ResponseEntity<Item>(savedItem, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PutMapping()
  public ResponseEntity<Item> updateUser(@RequestBody Item user) {
    Item updatedItem = itemService.update(user);
    if (updatedItem != null) {
      return new ResponseEntity(updatedItem, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-by-id/{id}")
  public ResponseEntity<Item> getById(@PathVariable Integer id) {
    Item itemFromDB = itemService.findById(id);
    if (itemFromDB != null){
      return new ResponseEntity(itemFromDB, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-by-code/{code}")
  public ResponseEntity<Item> getById(@PathVariable String code) {
    List<Item> itemsFromDB = itemService.findByItemCode(code);
    if (itemsFromDB != null){
      return new ResponseEntity(itemsFromDB, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-all")
  public ResponseEntity<List<Item>> getAll() {
    List<Item> items = itemService.findAll();
    return new ResponseEntity(items, HttpStatus.OK);
  }

  @DeleteMapping(path="/{id}")
  public ResponseEntity delete(@PathVariable Integer id) {
    Item itemToDelete = itemService.findById(id);
    if (itemToDelete == null) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } else {
      itemService.delete(itemToDelete);
      return new ResponseEntity(HttpStatus.OK);
    }
  }

}
