package com.letseatall.letseatall.data.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    @ToString.Exclude
    private Restaurant restaurant;

    @Column(nullable = false)
    String name;
    @Column
    int price;
    @Column
    int category;

    @OneToMany(mappedBy = "Menu", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Review> reviews= new ArrayList<>();
}
