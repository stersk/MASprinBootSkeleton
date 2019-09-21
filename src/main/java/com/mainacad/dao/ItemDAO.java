package com.mainacad.dao;

import com.mainacad.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemDAO extends JpaRepository<Item, Integer> {
  List<Item> findItemsByItemCode(String itemCode);
}