package com.letseatall.letseatall.data.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
    @ToString.Exclude
    private Category category;

    @Column(nullable = false)
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    public void setRestaurant(Restaurant restaurant){
        if (restaurant!=null)
            restaurant.removeMenu(this);
        this.restaurant=restaurant;
        restaurant.addMenu(this);
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Franchise franchise;

    public void setFranchise(Franchise franchise){
        if(franchise != null)
            franchise.removeMenu(this);
        this.franchise=franchise;
        this.franchise.addMenu(this);
    }
    @OneToMany(mappedBy = "menu",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Review> reviewList=new ArrayList<>();

    public void addReview(Review review){
        reviewList.add(review);
    }
    public void removeReview(Review review){
        reviewList.remove(review);
    }

    public Menu(){}
    public Menu(String name, int price, int score, Category category){
        this.name=name;
        this.price=price;
        this.score=score;
        this.category=category;
    }
}
