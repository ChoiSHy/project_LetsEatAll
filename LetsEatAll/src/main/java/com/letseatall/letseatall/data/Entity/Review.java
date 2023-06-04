package com.letseatall.letseatall.data.Entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long uid;   // writer User id
    @Column(nullable = false)
    private String writer;
    @Column(nullable = false)
    private Long mid;
    @Column(nullable = false)
    private String menu;
    @Column(nullable = false)
    private String title;
    @Column
    private String content;
    @Column(nullable = false)
    private int score;
    @Column(nullable = false)
    private int recCnt;
    @Column
    private Long pid;   // photo id

}
