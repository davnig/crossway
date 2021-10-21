package it.units.crossway.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.units.crossway.server.model.dto.StonePlacementIntent;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.GameRepository;
import it.units.crossway.server.repository.PlayerRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebSocketTests {

    @LocalServerPort
    private Integer port;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void when_postStonePlacementIntent_should_respondWith200AndSendMessageToSubscribedClients() throws Exception {
        BlockingQueue<StonePlacementIntent> blockingQueue = new ArrayBlockingQueue<>(1);
        String uuid = UUID.randomUUID().toString();
        Player whiteP = new Player("whiteP");
        Player blackP = new Player("blackP");
        Game game = new Game();
        game.setUuid(uuid);
        game.setWhitePlayerNickname(whiteP.getNickname());
        game.setBlackPlayerNickname(blackP.getNickname());
        game.setGameStatus(GameStatus.IN_PROGRESS);
        playerRepository.save(whiteP);
        gameRepository.save(game);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(
                1,
                2,
                "whiteP"
        );
        final MvcResult[] mvcResult = new MvcResult[1];
        StompSessionHandler stompSessionHandler = new StompSessionHandlerAdapter() {
            @SneakyThrows
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return StonePlacementIntent.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        blockingQueue.add((StonePlacementIntent) payload);
                    }
                });
                ObjectMapper om = new ObjectMapper();
                try {
                    mvcResult[0] = mvc.perform(post("/games/{uuid}/play", uuid)
                                    .content(om.writeValueAsString(stonePlacementIntent))
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andReturn();
                    System.out.println(mvcResult[0].getResponse().getContentAsString());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        stompClient.connect(getWsEndpoint(), stompSessionHandler);
        // block until available or expired timeout
        StonePlacementIntent response = blockingQueue.poll(2, TimeUnit.SECONDS);
        assertEquals(stonePlacementIntent, response);
        assertEquals(200, mvcResult[0].getResponse().getStatus());
    }

    private String getWsEndpoint() {
        return String.format("ws://localhost:%d/endpoint", port);
    }

}
