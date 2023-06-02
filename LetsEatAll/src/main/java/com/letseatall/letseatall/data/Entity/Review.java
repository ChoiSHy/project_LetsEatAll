package com.letseatall.letseatall.data.Entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

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
    @Column
    private String pid;   // photo id



}
