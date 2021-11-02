package it.units.crossway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@SpringBootApplication
@EnableFeignClients
public class ClientApplication {

    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        GameHandler gameHandler = (GameHandler) context.getBean("gameHandler");
        gameHandler.init();
    }

    @EventListener(SessionConnectedEvent.class)
    public void handleSessionConnected() {
        System.out.println("connesso");
    }

    @EventListener(SessionConnectEvent.class)
    public void handleSessionDisconnect() {
        System.out.println("disconnesso");
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
