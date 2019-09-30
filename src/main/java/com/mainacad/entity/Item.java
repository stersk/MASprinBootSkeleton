package com.mainacad.entity;

import lombok.*;
import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_code")
    @EqualsAndHashCode.Exclude
    private String itemCode;

    @Column(name = "item_name")
    @EqualsAndHashCode.Exclude
    private String name;

    @Column(name = "price")
    @EqualsAndHashCode.Exclude
    private Integer price;

    public Item(String itemCode, String name, Integer price) {
        this.itemCode = itemCode;
        this.name = name;
        this.price = price;
    }
}
