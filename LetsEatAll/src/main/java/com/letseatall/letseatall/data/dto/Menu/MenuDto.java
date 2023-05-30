package com.letseatall.letseatall.data.dto.Menu;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MenuDto {
    private Long rid;
    private String name;
    private int price;
    private int category;
}
