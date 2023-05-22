package com.letseatall.letseatall.data.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    Long cid;   // writer client id
    @Column(nullable = false)
    String title;
    @Column
    String content;
    @Column(nullable = false)
    int score;
    @Column(nullable = false)
    int recCnt;
    @Column
    Long pid;   // photo id
}
