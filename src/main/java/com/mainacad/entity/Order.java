package com.mainacad.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = Item.class)
    @EqualsAndHashCode.Exclude
    private Item item;

    @Column(name = "amount")
    @EqualsAndHashCode.Exclude
    private Integer amount;

    @ManyToOne(targetEntity = Cart.class)
    @EqualsAndHashCode.Exclude
    private Cart cart;

    public Order(Item item, Integer amount, Cart cart) {
        this.item = item;
        this.amount = amount;
        this.cart = cart;
    }
}
