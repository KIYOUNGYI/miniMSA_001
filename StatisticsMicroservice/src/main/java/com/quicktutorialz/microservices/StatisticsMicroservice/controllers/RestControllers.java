package com.quicktutorialz.microservices.StatisticsMicroservice.controllers;

import com.quicktutorialz.microservices.StatisticsMicroservice.services.StatisticsService;
import com.quicktutorialz.microservices.StatisticsMicroservice.utilities.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllers {


    @Autowired
    StatisticsService statisticsService;

    @CrossOrigin
    @RequestMapping("/getStatistics")
    public ResponseEntity<JsonResponseBody> getStatistics(@RequestParam(value="jwt") String jwt, @RequestParam(value="email")String email){
        return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(),statisticsService.getStatistics(jwt,email)));
    }

    @RequestMapping("/alivecheck")
//  @ResponseBody
    public String hello() {
        return "statistics hello";   //"hello"-> hello.jsp  HTML page(ViewResolver)
        // RestController를 사용하므로 이제 jsp 파일로 생성되는 걱적 ㄴㄴ
    }

    @RequestMapping("/test")
    public String test(){
        return "Statistics Microservice works correctly.";
    }



}
