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
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login")
    @EqualsAndHashCode.Exclude
    private String login;

    @Column(name = "password")
    @EqualsAndHashCode.Exclude
    private String password;

    @Column(name = "first_name")
    @EqualsAndHashCode.Exclude
    private String firstName;

    @Column(name = "last_name")
    @EqualsAndHashCode.Exclude
    private String lastName;

    public User(String login, String password, String firstName, String lastName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
