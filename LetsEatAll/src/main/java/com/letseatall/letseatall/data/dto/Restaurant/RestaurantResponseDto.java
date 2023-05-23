package com.letseatall.letseatall.data.dto.Restaurant;

import com.letseatall.letseatall.data.dto.Menu.MenuElement;

import java.util.List;

public class RestaurantResponseDto {
    private String name;
    private String addr;
    private int category;
    private Long fid;
    private List<MenuElement> menus;

    /* 좌표 */
    private double xpos;
    private double ypos;
}
