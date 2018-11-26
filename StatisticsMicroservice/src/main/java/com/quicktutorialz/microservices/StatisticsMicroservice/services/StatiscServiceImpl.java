package com.quicktutorialz.microservices.StatisticsMicroservice.services;

import com.quicktutorialz.microservices.StatisticsMicroservice.daos.StatisticsDao;
import com.quicktutorialz.microservices.StatisticsMicroservice.entities.Statistics;
import com.quicktutorialz.microservices.StatisticsMicroservice.utilities.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StatiscServiceImpl implements StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatiscServiceImpl.class);

    @Autowired
    StatisticsDao statisticsDao;

    @Override
    public List<Statistics> getStatistics(String jwt, String email) {

        //1. call ToDoMicroservice to get List<ToDos> (we need to result logged passing a valid JWT in the HEADER of the request)
        //   we get the valid jwt from the client (PostMan or Browser with HTML interface) which has done the login (first call)

        List <LinkedHashMap> todos = getNewDataFromToDoMicroservice(jwt);
        log.info("[getStatistics] todos : "+todos.toString());
        //2. calculate the statistics on the List of ToDos received

        String statisticsDesc = "No Statistics available";

        if(todos!=null || todos.size()>0){
            int lowPriorityToDos = 0;
            int highPriorityToDos = 0;
            for(int i=0;i<todos.size();i++){
                LinkedHashMap todo = todos.get(i);
                String priority = (String) todo.get("priority");
                if("low".equals(priority)) lowPriorityToDos++;
                if("high".equals(priority))  highPriorityToDos++;
            }
            statisticsDesc = "Yo have <b> " +lowPriorityToDos+" low priority</b> ToDos and <b> "+highPriorityToDos+"</b> high priority</b> ToDos. ";
        }

        //3. save the new statistic into statisticdb if a day has passed from the last update
        List<Statistics> statistics = statisticsDao.getLastStatistics(email);
        log.info("[getStatistics] statistics :"+ statistics.toString());
//        if(statistics.size()>0){
              Date now = new Date();
//              long diff = now.getTime() - statistics.get(0).getDate().getTime();
//              long days = diff/(24 * 60 * 1000);
//              log.info("[getStatistics] days :"+String.valueOf(days));
//            if(days>1){
                Statistics st = new Statistics(null,statisticsDesc,new Date(),email);
                log.info("[getStatistics] st : "+st.toString());
                Object obj = statisticsDao.save(st);
                log.info("[getStatistics] obj : "+obj.toString());
                statistics.add((Statistics)obj);
                log.info("[getStatistics] statistics : "+statistics.toString());
//            }
//        }
        //4. return the List of statistics

        return statistics;
    }

    private List<LinkedHashMap> getNewDataFromToDoMicroservice(String jwt){
        //1. prepare the header of request with jwt
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<String,String>();
        headers.add("jwt",jwt);
        // attach to http request.
        HttpEntity<?> request = new HttpEntity(String.class,headers);


        //2. call ToDoMicroservice getting ResponseEntity<JsonResponseBody> as HTTP response
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonResponseBody> responseEntity = restTemplate.exchange("http://localhost:8383/showToDos",HttpMethod.POST,request,JsonResponseBody.class);

        //3. extract from JsonResponseBody (recognized as java Object from JacksonLibrary) the "response" attribute.
        //This response attribute for "/showToDos" is a List<ToDos>. But in this microservice we haven't defined ToDo.java
        //then JacksonLibrary cannot interpret it as a List<ToDos> but it will interpret it as a List<LinkedHashMap>
        List<LinkedHashMap> toDoList = (List) responseEntity.getBody().getResponse();
        log.info("[getNewDataFromToDoMicroservice] toDoList :"+toDoList.toString());

        return toDoList;
    }
}
