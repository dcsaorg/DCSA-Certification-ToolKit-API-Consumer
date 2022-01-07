package org.dcsa.ctk.consumer;

import org.dcsa.tnt.Application;
import org.dcsa.tnt.controller.EventController;
import org.dcsa.tnt.controller.TNTEventSubscriptionTOController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@ServletComponentScan(basePackages="org.dcsa")
@SpringBootApplication
@ComponentScan(basePackages="org.dcsa")
public class CTKApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CTKApplication.class);
		//SpringApplication.run(CTKApplication.class, args);

		application.addListeners((ApplicationListener<ContextClosedEvent>) event -> {
			//log.info("Shutdown process initiated...");
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(5));
			} catch (InterruptedException e) {
				//log.error("Exception is thrown during the ContextClosedEvent", e);
			}
			//log.info("Graceful Shutdown is processed successfully");
			System.out.println("Graceful Shutdown is processed successfully");
		});
		application.run(args);
	}


}
