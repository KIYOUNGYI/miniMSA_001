package com.quicktutorialz.microservices.StatisticsMicroservice.services;

import com.quicktutorialz.microservices.StatisticsMicroservice.entities.Statistics;

import java.util.List;

public interface StatisticsService {
    List<Statistics> getStatistics(String jwt,String email);
}
