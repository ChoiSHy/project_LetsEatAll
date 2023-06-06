package com.letseatall.letseatall.data.dto.Category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class CategoryDto {
    private int id;
    private String name;
    private String url;
}
