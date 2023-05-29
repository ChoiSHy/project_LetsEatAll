package com.letseatall.letseatall.data.dto.Restaurant;

import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Builder
@Getter
@Setter
@ToString
public class FranchiseResponseDto {
    private Long id;
    private String name;
    private String category;

}
