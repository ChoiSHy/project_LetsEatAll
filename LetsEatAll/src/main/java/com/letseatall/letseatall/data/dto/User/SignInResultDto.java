package com.letseatall.letseatall.data.dto.User;

import lombok.*;

// 예제 13.30
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {

    private String token;
    private String name;
    private String id;

    @Builder
    public SignInResultDto(boolean success, int code, String msg, String token, String name, String id) {
        super(success, code, msg);
        this.token = token;
        this.name = name;
        this.id = id;
    }

}