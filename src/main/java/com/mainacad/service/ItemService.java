package com.mainacad.service;

import com.mainacad.dao.ItemDAO;
import com.mainacad.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
  @Autowired
  private ItemDAO itemDAO;

  public Item save(Item item){
    return itemDAO.save(item);
  }

  public Item update(Item item){
    Optional<Item> checkedItem = itemDAO.findById(item.getId());
    if (checkedItem.isPresent()) {
      return itemDAO.saveAndFlush(item);
    }

    return null;
  }

  public Item findById(Integer id){
    return itemDAO.findById(id).orElse(null);
  }

  public List<Item> findByItemCode(String itemCode){
    return itemDAO.findItemsByItemCode(itemCode);
  }

  public List<Item> findAll(){
    return itemDAO.findAll();
  }

  public void delete(Item item) {
    itemDAO.delete(item);
  }
}
