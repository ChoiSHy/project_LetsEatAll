package com.letseatall.letseatall.data.dto.Restaurant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class FranchiseResponseDto {
    private Long id;
    private String name;
    private String category;

}
