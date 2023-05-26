package com.letseatall.letseatall.data.dto.Client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClientDto {
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String id;
    private String pw;
}
