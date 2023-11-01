package com.letseatall.letseatall.data.dto.User;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignInRequestDto {

    private String id;

    private String password;

}