package com.kiyoung.msa.gateway.APIGateway.controller;

import com.kiyoung.msa.gateway.APIGateway.utilities.JsonResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllers {

    @RequestMapping(name="/apigateway/call/")
    public JsonResponseBody hello(){
        return new JsonResponseBody(200,"hello gateway!");
    }
}
