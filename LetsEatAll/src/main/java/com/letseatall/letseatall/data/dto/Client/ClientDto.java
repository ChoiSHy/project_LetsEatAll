package com.letseatall.letseatall.data.dto.Client;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClientDto {
    private String name;
    private LocalDate birthDate;
    private String id;
    private String pw;
}
