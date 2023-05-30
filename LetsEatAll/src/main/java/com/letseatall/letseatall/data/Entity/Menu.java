package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column
    private int price;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="category_id")
    @ToString.Exclude
    private Category category;

    @Column(nullable = false)
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant_id")
    @ToString.Exclude
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="franchise_id")
    @ToString.Exclude
    private Franchise franchise;
}
