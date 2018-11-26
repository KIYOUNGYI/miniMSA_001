package com.kiyoung.msa.nameserver.EurekaNameServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaNameServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaNameServerApplication.class, args);
	}
}
