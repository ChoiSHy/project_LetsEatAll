package com.letseatall.letseatall.data.Entity.image;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review;
import lombok.*;
import org.hibernate.id.UUIDGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    private String store_file_name;
    @Column(nullable = false)
    private String upload_file_name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;
    public void setMenu(Review review){
        if(this.review!=null)
            review.removeImg(this);
        this.review=review;
        review.addImg(this);
    }
    @PrePersist
    public void setStoredName(){
        store_file_name = UUID.randomUUID().toString();
    }
}
