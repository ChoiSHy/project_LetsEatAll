package com.letseatall.letseatall.data.Entity;

import com.letseatall.letseatall.data.Entity.image.ImageFile;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;
    public void setWriter(User writer){
        if(this.writer!=null)
            writer.removeReview(this);
        this.writer=writer;
        writer.addReview(this);
    }
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;
    public void setMenu(Menu menu){
        if(this.menu!=null)
            menu.removeReview(this);
        this.menu=menu;
        menu.addReview(this);
    }
    @Column(nullable = false)
    private String title;
    @Column
    private String content;
    @Column(nullable = false)
    private int score;
    @Column(nullable = false)
    private int recCnt;
    @OneToMany
    private List<ImageFile> imgList=new ArrayList<>();
    public void addImg(ImageFile img){
        imgList.add(img);
    }
    public void removeImg(ImageFile img){
        imgList.remove(img);
    }
}
