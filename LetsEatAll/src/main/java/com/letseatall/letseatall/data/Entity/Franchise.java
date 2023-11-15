package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "franchise")
public class Franchise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Restaurant> chains = new ArrayList<>();

    public Franchise() {
    }

    public Franchise(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void addChain(Restaurant restaurant) {
        chains.add(restaurant);
    }

    public void removeChain(Restaurant restaurant) {
        chains.remove(restaurant);
    }

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Menu> menus = new ArrayList<>();

    public void addMenu(Menu menu) {
        menus.add(menu);
    }

    public void removeMenu(Menu menu) {
        menus.remove(menu);
    }

}
