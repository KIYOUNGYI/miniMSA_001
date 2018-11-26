package com.kiyoung.msa.gateway.APIGateway.utilities;

import lombok.Getter;
import lombok.Setter;

public class JsonJWTResponse {

    @Getter
    @Setter
    private int server;
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private String jwt;

    public JsonJWTResponse(){

    }

    public JsonJWTResponse(int server,String message,String jwt){
        this.server = server;
        this.message = message;
        this.jwt = jwt;
    }

    @Override
    public String toString() {
        return "JsonJWTResponse{" +
                "server=" + server +
                ", message='" + message + '\'' +
                ", jwt='" + jwt + '\'' +
                '}';
    }
}
