package com.revature.nflfantasydraft.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EBotException extends RuntimeException {
    public EBotException(String message) {
        super(message);
    }
}   