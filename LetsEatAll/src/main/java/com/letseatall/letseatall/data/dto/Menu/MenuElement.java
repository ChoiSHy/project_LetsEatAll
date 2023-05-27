package com.letseatall.letseatall.data.dto.Menu;

import com.letseatall.letseatall.data.Entity.Menu;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.query.Param;

@Getter
@Setter
public class MenuElement {
    private Long id;
    private String name;
    private int price;
    private int category;

    public MenuElement(Menu menu){
        this.id= menu.getId();
        this.name=menu.getName();
        this.price= menu.getPrice();
        this.category= menu.getCategory();
    }
}
