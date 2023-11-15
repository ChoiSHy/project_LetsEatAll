package com.letseatall.letseatall.data.Entity.menu;

import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class MenuImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String storedName;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="menu_id")
    @ToString.Exclude
    private Menu menu;

}
