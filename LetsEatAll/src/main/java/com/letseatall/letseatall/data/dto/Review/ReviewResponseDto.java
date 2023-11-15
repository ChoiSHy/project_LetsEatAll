package com.letseatall.letseatall.data.dto.Review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
public class ReviewResponseDto {
    private Long review_id;
    private String content;
    private int score;
    private int like_count;
    private int unlike_count;
    private Long menu_id;
    private String menu_name;
    private String writer;
    private Long user_id;
    private String img_url;
    private LocalDateTime updatedAt;
}
