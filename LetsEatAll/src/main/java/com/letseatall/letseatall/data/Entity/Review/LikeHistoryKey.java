package com.letseatall.letseatall.data.Entity.Review;


import lombok.Data;

import java.io.Serializable;
@Data
public class LikeHistoryKey implements Serializable {
    Long reviewId;
    Long userId;
}
