package com.srbruninho.contasapagar.infrastructure.exception;

import lombok.*;

@Data
@AllArgsConstructor
public class BusinessErrorResponse {

    private String message;
    private int statusCode;

    public BusinessErrorResponse(String message){
        this.message = message;
    }
}
