package com.letseatall.letseatall.data.Entity.image;

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
    Long id;
    @Column(nullable = false, name="storedName")
    private String storedFileName;
    @Column(nullable = false, name="uploadName")
    private String uploadedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    @ToString.Exclude
    private Review review;
    public void setReview(Review review){
        if(this.review!=null)
            review.removeImg(this);
        this.review=review;
        review.addImg(this);
    }
    @PrePersist
    public void setStoredName(){
        storedFileName = UUID.randomUUID().toString();
    }
}
