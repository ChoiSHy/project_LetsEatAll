package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "youtube")
public class Youtube {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String content;
    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name="menu_id")
    private Menu menu;
/*
    public void setMenu(Menu menu){
        if(menu != null){
            menu.removeYoutube(this);
        }
        this.menu=menu;
        menu.addYoutube(this);
    }
*/
    public Youtube(){}
    public Youtube(String url, String content){
        this.url=url;
        this.content=content;
    }

}
