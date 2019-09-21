package com.mainacad.service;

import com.mainacad.dao.CartDAO;
import com.mainacad.dao.OrderDAO;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartService {
  @Autowired
  private CartDAO cartDAO;

  @Autowired
  private OrderDAO orderDAO;

  public Cart createCartForUser(User user){
    Cart createdCart = findOpenCartByUser(user);

    if (createdCart == null) {
      createdCart = new Cart();
      createdCart.setCreationTime(new Date().getTime());
      createdCart.setClosed(Boolean.FALSE);
      createdCart.setUser(user);

      createdCart = cartDAO.save(createdCart);
    }

    return createdCart;
  }

  public Cart findById(Integer id) {
    return cartDAO.findById(id);
  }

  public Cart findOpenCartByUser(User user){
    return cartDAO.findOpenCartByUser(user);
  }

  public Cart close(Cart cart){
    cart.setClosed(true);
    return cartDAO.update(cart);
  }

  public Integer getCartSum(Cart cart){
    return cartDAO.getCartSum(cart);
  }

  public void deleteCart(Cart cart) {
    List<Order> cartOrders = orderDAO.findByCart(cart);

    cartOrders.stream().forEach(order -> orderDAO.delete(order));
    cartDAO.delete(cart);
  }

  public List<Order> getOrdersFromOpenCartByUser(User user) {
    List<Order> orders = new ArrayList<>();

    Cart openCart = cartDAO.findOpenCartByUser(user);
    if (openCart != null) {
      orders = orderDAO.findByCart(openCart);
    }

    return orders;
  }
}
