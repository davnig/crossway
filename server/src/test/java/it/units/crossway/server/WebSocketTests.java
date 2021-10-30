package it.units.crossway.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.dto.StonePlacementIntent;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.GameRepository;
import it.units.crossway.server.repository.PlayerRepository;
import lombok.NonNull;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
            public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return StonePlacementIntent.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
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

    @Test
    void when_putJoinGameIntent_should_sendMessageToOpponent() throws InterruptedException {
        BlockingQueue<StompHeaders> blockingQueue = new ArrayBlockingQueue<>(1);
        String uuid = UUID.randomUUID().toString();
        Player blackP = new Player("blackP");
        Game game = new Game();
        game.setUuid(uuid);
        game.setBlackPlayerNickname(blackP.getNickname());
        game.setGameStatus(GameStatus.CREATED);
        playerRepository.save(blackP);
        gameRepository.save(game);
        PlayerDto whiteP = new PlayerDto("whiteP");
        final MvcResult[] mvcResult = new MvcResult[1];
        StompSessionHandler stompSessionHandler = new StompSessionHandlerAdapter() {
            @SneakyThrows
            @Override
            public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        blockingQueue.add(headers);
                    }
                });
                ObjectMapper om = new ObjectMapper();
                try {
                    mvcResult[0] = mvc.perform(put("/games/{uuid}", uuid)
                                    .content(om.writeValueAsString(whiteP))
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andReturn();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        stompClient.connect(getWsEndpoint(), stompSessionHandler);
        StompHeaders responseHeaders = blockingQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(responseHeaders);
        assertTrue(responseHeaders.containsKey("join-event"));
        assertEquals(whiteP.getNickname(), responseHeaders.getFirst("join-event"));
        assertEquals(200, mvcResult[0].getResponse().getStatus());
    }

    @Test
    void when_winGame_should_sendMessageToSubscribedClients() throws InterruptedException {
        BlockingQueue<StompHeaders> blockingQueue = new ArrayBlockingQueue<>(1);
        String uuid = UUID.randomUUID().toString();
        Player whiteP = new Player("whiteP");
        Player blackP = new Player("blackP");
        Game game = new Game();
        game.setUuid(uuid);
        game.setWhitePlayerNickname(whiteP.getNickname());
        game.setBlackPlayerNickname(blackP.getNickname());
        game.setGameStatus(GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        PlayerDto playerDto = new PlayerDto(whiteP.getNickname());
        final MvcResult[] mvcResult = new MvcResult[1];
        StompSessionHandler stompSessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return StonePlacementIntent.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        blockingQueue.add(headers);
                    }
                });
                ObjectMapper om = new ObjectMapper();
                try {
                    mvcResult[0] = mvc.perform(put("/games/{uuid}/win", uuid)
                                    .content(om.writeValueAsString(playerDto))
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
        StompHeaders responseHeaders = blockingQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(responseHeaders);
        assertTrue(responseHeaders.containsKey("win-event"));
        assertEquals(whiteP.getNickname(), responseHeaders.getFirst("win-event"));
        assertEquals(200, mvcResult[0].getResponse().getStatus());
    }

    private String getWsEndpoint() {
        return String.format("ws://localhost:%d/endpoint", port);
    }

}
