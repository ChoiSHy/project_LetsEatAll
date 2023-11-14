package com.letseatall.letseatall.data.dto.Restaurant;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Builder
@Data
public class RestaurantResponseDto {
    private Long restaurant_id;
    private String restaurant_name;
    private String restaurant_addr;
    private String restaurant_category;
    private double restaurant_score;
    private String franchise;
    private List<MenuListDto> menuDtoList;

}
