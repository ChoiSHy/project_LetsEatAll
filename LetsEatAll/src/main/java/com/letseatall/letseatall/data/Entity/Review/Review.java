package com.letseatall.letseatall.data.Entity.Review;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User writer;
    public void setWriter(User writer){
        if(this.writer!=null)
            writer.removeReview(this);
        this.writer=writer;
        writer.addReview(this);
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Menu menu;
    public void setMenu(Menu menu){
        if(this.menu!=null)
            menu.removeReview(this);
        this.menu=menu;
        menu.addReview(this);
    }
    @Column
    private String content;
    @Column(nullable = false)
    private int score;
    @Column(nullable = false)
    private int like_cnt;
    @Column(nullable = false)
    private int unlike_cnt;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "review")
    @ToString.Exclude
    private ImageFile img;

    public void setImg(ImageFile img){
        this.img= img;
        if(img != null)
            img.setReview(this);
    }
}
