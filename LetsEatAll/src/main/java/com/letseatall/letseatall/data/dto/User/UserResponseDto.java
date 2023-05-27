package com.letseatall.letseatall.data.dto.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letseatall.letseatall.data.Entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private int score;
}
