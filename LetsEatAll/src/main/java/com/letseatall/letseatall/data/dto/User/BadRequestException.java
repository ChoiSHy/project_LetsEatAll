package com.letseatall.letseatall.data.dto.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT, reason="Already exists")
public class BadRequestException extends RuntimeException{

    public BadRequestException(String msg){
        super(msg);
    }
}
