package com.letseatall.letseatall.data.Entity.Review;

import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import lombok.*;

import javax.persistence.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@IdClass(LikeHistoryKey.class)
public class LikeHistory {
    @Id
    @Column(name="review_id", nullable = false)
    Long reviewId;
    @Id
    @Column(name="user_id", nullable = false)
    Long userId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId("reviewId")
    @JoinColumn(name = "review_id")
    Review review;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;


}
