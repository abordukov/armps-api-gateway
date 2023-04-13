package ru.invitro.armpsapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.invitro.armpsapigateway.configuration")
@EnableEurekaServer
public class ArmpsApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArmpsApiGatewayApplication.class, args);
	}
}
