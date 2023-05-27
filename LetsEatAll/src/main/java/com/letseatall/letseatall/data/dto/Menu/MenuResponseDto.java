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
    private String name;
    private int price;
    private int category;
    private int score;
    //private List<ReviewElement> reviews;
    private String Yurl;    //youtube url
    private String Ysum;    //YouTube summary
}
