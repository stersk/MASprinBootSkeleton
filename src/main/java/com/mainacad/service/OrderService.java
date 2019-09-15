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

   /* public static Order createOrderByItemAndUser(Item item, Integer amount, User user){
        Order order = new Order();
        order.setItemId(item.getId());
        order.setAmount(amount);
        // get or create open cart
        Cart cart = CartService.findOpenCartByUser(user.getId());
        if (cart == null) {
            cart = CartService.createCartForUser(user.getId());
        }
        order.setCartId(cart.getId());
        return OrderDAO.create(order);
    }

    public static Order addItemToOrder(Item item, User user){
        Order existingOrder = CartService.getOrdersFromOpenCartByUser(user.getId()).stream().filter(order -> order.getItemId().equals(item.getId())).findAny().orElse(null);
        if (existingOrder == null) {
            existingOrder = createOrderByItemAndUser(item, 1, user);
        } else {
            existingOrder.setAmount(existingOrder.getAmount() + 1);
            OrderDAO.update(existingOrder);
        }

        return existingOrder;
    }*/

    public List<Order> getOrdersByCart(Cart cart){
        return orderDAO.findByCart(cart);
    }

    /*public static Order findById(Integer id) {
        return OrderDAO.findById(id);
    }

    public static List<Order> findClosedOrdersByUserAndPeriod(User user, Long from, Long to){
        return OrderDAO.findClosedOrdersByUserIdAndPeriod(user.getId(), from, to);
    }*/

    public void deleteOrder(Order order) {
        orderDAO.delete(order);
    }

/*    public static Order updateItemAmountInOrder(Integer id, Integer amount) {
        Order order = OrderDAO.findById(id);
        order.setAmount(amount);
        return OrderDAO.update(order);
    }*/
}
