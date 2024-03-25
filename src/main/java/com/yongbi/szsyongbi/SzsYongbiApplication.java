package com.yongbi.szsyongbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SzsYongbiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SzsYongbiApplication.class, args);
	}

}
