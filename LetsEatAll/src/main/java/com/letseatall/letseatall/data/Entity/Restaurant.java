package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@ToString
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
    private double score;
    @Column
    private String addr;
    @ManyToOne
    private Category category;
    @ManyToOne
    private Franchise franchise;
    public void setFranchise(Franchise franchise){
        if (franchise!= null)
            franchise.removeChain(this);
        this.franchise=franchise;
        franchise.addChain(this);
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Menu> menus = new ArrayList<>();
    public void addMenu(Menu menu){menus.add(menu);}
    public void removeMenu(Menu menu){menus.remove(menu);}
}
