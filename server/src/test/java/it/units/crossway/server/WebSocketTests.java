package it.units.crossway.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
public class WebSocketTests {

    @LocalServerPort
    private Integer port;
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private GameRepository gameRepository;
//    @Autowired
//    private PlayerRepository playerRepository;

    private BlockingQueue<String> blockingQueue;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        blockingQueue = new ArrayBlockingQueue<>(1);
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @Test
    void test_ws_setup() throws Exception {
//        Player whitePlayer = new Player("whiteP");
//        Player blackPlayer = new Player("blackP");
//        Game game = new Game();
//        String uuid = UUID.randomUUID().toString();
//        game.setUuid(uuid);
//        game.setWhitePlayer(whitePlayer.getNickname());
//        game.setBlackPlayer(blackPlayer.getNickname());
//        game.setGameStatus(GameStatus.IN_PROGRESS);
//        gameRepository.save(game);
        StompSessionHandler stompSessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/greetings", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        blockingQueue.add((String) payload);
                    }
                });
                session.send("/app/greetings", "ciao");
            }
        };
        stompClient.connect(getWsEndpoint(), stompSessionHandler);
        // block until available
        String response = blockingQueue.take();
        assertEquals("ok", response);
//        PlayTurnIntent playTurnIntent = new PlayTurnIntent();
//        ObjectMapper om = new ObjectMapper();
//        session.subscribe("/topic/greetings", new DefaultStompFrameHandler());
//        mvc.perform(put("/games/{uuid}", uuid)
//                        .content(om.writeValueAsString(playTurnIntent))
//                        .contentType(MediaType.APPLICATION_JSON))
////                .andDo(print())
//                .andExpect(status().isOk());
//        assertEquals("ok", blockingQueue.poll(1, SECONDS));
    }

    private String getWsEndpoint() {
        return String.format("ws://localhost:%d/endpoint", port);
    }

}
