package com.letseatall.letseatall.data.dto.Review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
public class ReviewDto {
    private Long mid;
    private String content;
    private int score;
}
