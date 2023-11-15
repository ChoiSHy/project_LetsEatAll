package com.letseatall.letseatall.data.dto.Menu;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuElement {
    private Long id;
    private String name;
    private int price;
    private int category;
}
