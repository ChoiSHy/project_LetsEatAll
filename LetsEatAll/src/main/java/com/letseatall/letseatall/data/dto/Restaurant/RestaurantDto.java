package com.letseatall.letseatall.data.dto.Restaurant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestaurantDto {
    private String name;
    private String addr;
    private int category;
    private Long fid;
}
