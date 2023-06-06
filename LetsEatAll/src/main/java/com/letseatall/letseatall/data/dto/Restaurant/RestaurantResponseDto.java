package com.letseatall.letseatall.data.dto.Restaurant;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Builder
@Data
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String addr;
    private String category;
    private int score;
    private String franchise;

}
