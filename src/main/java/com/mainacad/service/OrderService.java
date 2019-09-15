package com.mainacad.service;

import com.mainacad.dao.OrderDAO;
import com.mainacad.model.Cart;
import com.mainacad.model.Item;
import com.mainacad.model.Order;
import com.mainacad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderDAO orderDAO;

    @Autowired
    CartService cartService;

    public Order createOrderByItemAndUser(Item item, Integer amount, User user){
        Order order = new Order();
        order.setItem(item);
        order.setAmount(amount);
        // get or create open cart
        Cart cart = cartService.findOpenCartByUser(user);
        if (cart == null) {
            cart = cartService.createCartForUser(user);
        }
        order.setCart(cart);
        return orderDAO.save(order);
    }

    public Order addItemToOrder(Item item, User user){
        Order existingOrder = cartService.getOrdersFromOpenCartByUser(user).stream().filter(order -> order.getItem().equals(item)).findAny().orElse(null);
        if (existingOrder == null) {
            existingOrder = createOrderByItemAndUser(item, 1, user);
        } else {
            existingOrder.setAmount(existingOrder.getAmount() + 1);
            orderDAO.update(existingOrder);
        }

        return existingOrder;
    }

    public List<Order> getOrdersByCart(Cart cart){
        return orderDAO.findByCart(cart);
    }

    public Order findById(Integer id) {
        return orderDAO.findById(id);
    }

    public void deleteOrder(Order order) {
        orderDAO.delete(order);
    }

    public Order updateItemAmountInOrder(Order order, Integer amount) {
        order.setAmount(amount);

        return orderDAO.update(order);
    }
}
