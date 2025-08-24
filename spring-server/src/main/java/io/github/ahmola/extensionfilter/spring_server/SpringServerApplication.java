package io.github.ahmola.extensionfilter.spring_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


/*
swagger : http://localhost:8080/swagger-ui.html
redis : http://localhost:6379
prometheus : http://localhost:9090
grafana : http://localhost:3000
 */
@EnableCaching
@SpringBootApplication
public class SpringServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringServerApplication.class, args);
	}

}
