package it.units.crossway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import it.units.crossway.client.model.Board;
import it.units.crossway.client.model.Player;
import it.units.crossway.client.model.PlayerColor;
import it.units.crossway.client.model.Turn;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.remote.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GameHandlerTests {

    @Mock
    private Player player;
    @Mock
    private Api api;
    private GameHandler gameHandler;
    private WireMockServer wireMockServer;

    void initWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(9111));
        wireMockServer.start();
    }

    Api buildAndReturnFeignClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .target(Api.class, "http://localhost:9111");
    }

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Turn turn = new Turn();
        Board board = new Board();
        gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getTurn().initFirstTurn();
        assertTrue(gameHandler.getBoard().getBoardState().isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Turn turn = new Turn();
        Board board = new Board();
        gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getTurn().initFirstTurn();
        Assertions.assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getCurrentPlayer());
    }

    @Test
    void whenPlayerInsertsNicknameShouldSendAddPlayerReq() throws JsonProcessingException {
        initWireMockServer();
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getPlayer().setNickname(nickname);
        PlayerDto playerDto = new PlayerDto(nickname);
        ObjectMapper om = new ObjectMapper();
        String jsonPlayerDto = om.writeValueAsString(playerDto);
        wireMockServer.stubFor(post(urlEqualTo("/players"))
                .withRequestBody(equalToJson(jsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonPlayerDto)));
        String nicknameInput = nickname + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(nicknameInput.getBytes()));
        ReflectionTestUtils.invokeMethod(gameHandler, "chooseNickname");
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/players")));
    }

    @Test
    void whenPlayerSelectsNewGameShouldSendCreateGameReq() throws JsonProcessingException {
        initWireMockServer();
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getPlayer().setNickname(nickname);
        GameCreationIntent gameCreationIntent = new GameCreationIntent(nickname);
        ObjectMapper om = new ObjectMapper();
        String jsonGameCreationIntent = om.writeValueAsString(gameCreationIntent);
        wireMockServer.stubFor(post(urlEqualTo("/games"))
                .withRequestBody(equalToJson(jsonGameCreationIntent))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonGameCreationIntent)));
        String createGameIntent = nickname + System.getProperty("line.separator") + "1" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(createGameIntent.getBytes()));
        ReflectionTestUtils.invokeMethod(gameHandler, "createNewGame");
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/games")));
    }

    @Test
    void whenPlayerSelectsJoinGameShouldSendGetAvailableGamesReq() throws JsonProcessingException {
        initWireMockServer();
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ObjectMapper om = new ObjectMapper();
        List<GameDto> listAvailableGames = new ArrayList<>();
        String jsonAvailableGames = om.writeValueAsString(listAvailableGames);
        wireMockServer.stubFor(get(urlEqualTo("/games"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonAvailableGames)));
        ReflectionTestUtils.invokeMethod(gameHandler, "getAllAvailableGamesDto");
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/games")));
    }

    @Test
    void whenPlayerSelectsAvailableGameShouldSendJoinGameReq() {
        // TODO
        fail();
    }

    @Test
    void whenCreateGameAndGameStartsThenPlayerShouldBeBlack() {
        // TODO
        fail();
    }

    @Test
    void whenJoinGameAndGameStartsThenPlayerShouldBeWhite() {
        // TODO
        fail();
    }

    @Test
    void whenPlayerPlaysTurnShouldSendStonePlacementIntent() {
        // TODO
        fail();
    }

    @Test
    void whenStonePlacementIntentIsReceivedThenBoardShouldBeUpdated() {
        // TODO
        fail();
    }

}
