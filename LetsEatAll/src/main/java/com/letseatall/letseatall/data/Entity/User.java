package com.letseatall.letseatall.data.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.letseatall.letseatall.data.Entity.Review.Review;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key
    private Long id;
    @Column(nullable = false)                           // 사용자 이름
    private String name;
    @Column(nullable = false)                           // 생년월일
    private LocalDate birthDate;
    @Column(nullable = false)                           // 점수
    private int score;

    @Column(nullable = false, unique = true)            // 로그인 id
    private String uid;

    @JsonProperty(access=Access.WRITE_ONLY)             // 로그인 비밀번호
    @Column(nullable = false)
    @ToString.Exclude
    private String password;
    @OneToMany(mappedBy = "writer",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude                                   // 리뷰 리스트
    private List<Review> reviewList = new ArrayList<>();

    // 리뷰 추가
    public void addReview(Review review) {
        reviewList.add(review);
    }
    // 리뷰 삭제
    public void removeReview(Review review){
        reviewList.remove(review);
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();     // 권한 목록

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 가지고 있는 권한 목록 반환
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Preference> preferences= new ArrayList<>();

    public void addPrefer(Preference prefer){preferences.add(prefer);}
    public void removePrefer(Preference prefer){preferences.remove(prefer);}
    @JsonProperty(access = Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        // 로그인 아이디 반환
        return this.uid;
    }
    @JsonProperty(access = Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되었는지 반환. true가 만료x
        return true;
    }
    @JsonProperty(access = Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겼는지 반환. true가 잠김x
        return true;
    }
    @JsonProperty(access = Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호가 만료되었는지 반환. true가 만료x
        return true;
    }
    @JsonProperty(access = Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        // 계정이 활성화돼 있는지 반환. true는 활성화
        return true;
    }
}