package com.letseatall.letseatall.data.dto.Menu;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuListDto {
    long menu_id;
    private String menu_name;
    private int menu_price;
    private String menu_category;
    private double menu_score;
    private String youtube_url;
    private String img_url;

}
