package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.Entity.menu.MenuImageFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@IdClass(PreferenceKey.class)
public class Preference {
    @Id
    @Column(name="user_id",nullable = false)
    Long userId;
    @Id
    @Column(name="cate_id", nullable = false)
    int categoryId;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name="user_id")
    @ToString.Exclude
    User user;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId("categoryId")
    @JoinColumn(name="cate_id")
    @ToString.Exclude
    Category category;
    public void setUser(User user) {
        if(this.user != null)
            user.removePrefer(this);
        this.user = user;
        user.addPrefer(this);
    }
    @Column(nullable = false)
    int score;
    public Preference(){
        this.score= 0;
    }
    public String toString(){
        return String.format("{user = %s, category = %s}", user.getUid(), category.getName());
    }

}
