package com.letseatall.letseatall.data.dto.Menu;

import com.letseatall.letseatall.data.dto.Review.ReviewElement;

import java.util.List;

public class MenuResponseDto {
    private Long rid;
    private String name;
    private int price;
    private int category;
    private List<ReviewElement> reviews;
    private String Yurl;    //youtube url
    private String Ysum;    //YouTube summary
}
