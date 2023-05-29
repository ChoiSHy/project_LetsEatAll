package com.letseatall.letseatall.data.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "franchise")
public class Franchise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
/*
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    private List<Restaurant> chains = new ArrayList<>();

    public void addChain(final Restaurant restaurant){
        chains.add(restaurant);
    }*/
}
