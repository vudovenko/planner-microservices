package ru.vudovenko.micro.planner.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@RefreshScope
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"ru.vudovenko.micro.planner"})
@EnableJpaRepositories(basePackages = {"ru.vudovenko.micro.planner.users"})
public class PlannerUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlannerUsersApplication.class, args);
	}

}
