package com.letseatall.letseatall.data.dto.Youtube;

import lombok.*;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeDto {
    private String videoUrl;
}
