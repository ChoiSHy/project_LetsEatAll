package com.letseatall.letseatall.data.dto.Youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Setter
@ToString
public class YoutubeDto {
    private String videoUrl;
}
