package it.units.crossway.client;

import it.units.crossway.client.model.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableFeignClients
public class ClientApplication {

    private ApplicationContext context;

    public static void main(String[] args) {
        Frame.clearConsole();
        SpringApplication.run(ClientApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        GameHandler gameHandler = (GameHandler) context.getBean("gameHandler");
        gameHandler.init();
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
