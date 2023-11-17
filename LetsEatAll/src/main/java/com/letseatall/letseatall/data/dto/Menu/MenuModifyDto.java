package com.letseatall.letseatall.data.dto.Menu;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuModifyDto {
    private long menu_id;
    private String name;
    private int price;
    private String info;
}
