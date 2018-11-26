package com.quicktutorialz.microservices.StatisticsMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class StatisticsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticsMicroserviceApplication.class, args);
	}
	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}
}
