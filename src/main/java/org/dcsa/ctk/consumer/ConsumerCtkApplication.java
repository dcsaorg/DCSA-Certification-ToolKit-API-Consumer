package org.dcsa.ctk.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@ServletComponentScan(basePackages="org.dcsa")
@SpringBootApplication
@ComponentScan(basePackages="org.dcsa")
public class ConsumerCtkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerCtkApplication.class, args);
	}

}
