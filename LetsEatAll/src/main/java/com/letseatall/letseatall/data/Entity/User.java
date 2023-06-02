package com.letseatall.letseatall.data.Entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false)
    private int score;
    @OneToMany(mappedBy = "writer",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Review> reviewList = new ArrayList<>();

    public void addReview(Review review) {
        reviewList.add(review);
    }
    public void removeReview(Review review){
        reviewList.remove(review);
    }

    @OneToOne(mappedBy = "user")
    private Login login;
}
