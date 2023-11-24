package com.letseatall.letseatall.data.dto.Menu;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.dto.Review.ReviewPageDto;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class MenuResponseDto {
    private Long rid;
    private String r_name;
    private String name;
    private int price;
    private String category;
    private double score;
    private String url;
    private String img_url;
    private String info;

}
