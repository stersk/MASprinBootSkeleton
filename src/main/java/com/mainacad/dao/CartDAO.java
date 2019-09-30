package com.mainacad.dao;

import com.mainacad.entity.Cart;
import com.mainacad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CartDAO extends JpaRepository<Cart, Integer> {
  Optional<Cart> findCartByClosedAndUser(Boolean closed, User user);

  @Query(nativeQuery = true, value =
          "SELECT SUM(orders.amount * items.price)  " +
                  "FROM orders " +
                  "JOIN items ON orders.item_id = items.id " +
                  "WHERE orders.cart_id=:cart_id")
  Integer getCartSum(@Param("cart_id") Integer cart_id);
}