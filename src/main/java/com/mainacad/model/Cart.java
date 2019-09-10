package com.mainacad.model;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_time")
    @EqualsAndHashCode.Exclude
    private Long creationTime;

    @Column(name = "closed")
    @EqualsAndHashCode.Exclude
    private Boolean closed;

    @ManyToOne(targetEntity = User.class)
    @EqualsAndHashCode.Exclude
    private User user;

    public Cart(Long creationTime, Boolean closed, User user) {
        this.creationTime = creationTime;
        this.closed = closed;
        this.user = user;
    }
}
