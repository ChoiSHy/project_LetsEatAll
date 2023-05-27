package com.letseatall.letseatall.data.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int score;
    @Column
    private String addr;
    @Column
    private int category;
    @Column
    private Long fid;       // franchise id

    @OneToMany(mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Menu> menus = new ArrayList<>();
}
