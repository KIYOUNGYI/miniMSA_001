package com.quicktutorialz.microservices.StatisticsMicroservice;

import com.quicktutorialz.microservices.StatisticsMicroservice.entities.Statistics;
import com.quicktutorialz.microservices.StatisticsMicroservice.services.StatiscServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StatisticServiceImplMainTest {


    static StatiscServiceImpl statiscService= new StatiscServiceImpl();

    public static void main(String[] args){
       List<Statistics> list = statiscService.getStatistics("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5a3kyNzk4QGdtYWlsLmNvbSIsImV4cCI6MTUzMjE1ODc0OCwibmFtZSI6IktpeW91bmcgWWkifQ.WZeh4JDaJvx9AUz51fT0FMuNnijIWb75TYI0JiflpsM","yky2798@gmail.com");
       System.out.println(list);
    }
}
