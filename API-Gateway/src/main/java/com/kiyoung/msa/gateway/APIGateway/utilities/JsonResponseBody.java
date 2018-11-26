package com.kiyoung.msa.gateway.APIGateway.utilities;

import lombok.Getter;
import lombok.Setter;

public class JsonResponseBody {

    @Getter
    @Setter
    private int server;
    @Getter
    @Setter
    private Object response;

    public JsonResponseBody(){}

    public JsonResponseBody(int server, Object response){
        this.server = server;
        this.response = response;
    }

}

// http response -> json Object ResponseEntity<JsonResponseBody>

// header (jwt)

// body - html page or a JsonMessage(int server,Object response)