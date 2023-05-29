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
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User writer;
    @ManyToOne
    @JoinColumn(name="menu_id")
    @ToString.Exclude
    private Menu menu;
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
