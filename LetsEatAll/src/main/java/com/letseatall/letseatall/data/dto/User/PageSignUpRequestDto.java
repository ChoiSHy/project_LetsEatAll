package com.letseatall.letseatall.data.dto.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PageSignUpRequestDto {
    private String id;
    private String password;
    private String name;
    private String birthDate;
}
