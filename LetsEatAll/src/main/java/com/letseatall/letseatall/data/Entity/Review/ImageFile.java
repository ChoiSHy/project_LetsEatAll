package com.letseatall.letseatall.data.Entity.Review;

import com.letseatall.letseatall.data.Entity.Review.Review;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    @ToString.Exclude
    private Review review;
}
