package com.letseatall.letseatall.data.dto.Restaurant;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Builder
@Data
public class RestaurantResponseDto {
    private String name;
    private String addr;
    private int category;
    private int score;
    private Long fid;
    private List<MenuElement> menus= new ArrayList<>();

    public void add(Menu menu){
        menus.add(new MenuElement(menu));
    }

}
