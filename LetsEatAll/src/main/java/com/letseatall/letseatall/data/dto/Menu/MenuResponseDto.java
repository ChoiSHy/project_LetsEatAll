package com.letseatall.letseatall.data.dto.Menu;

import com.letseatall.letseatall.data.dto.Review.ReviewElement;
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
    private String img;
    private String info;
}
