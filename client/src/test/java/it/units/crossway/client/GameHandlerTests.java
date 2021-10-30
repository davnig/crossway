package it.units.crossway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import it.units.crossway.client.model.*;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.model.dto.StonePlacementIntentDto;
import it.units.crossway.client.remote.Api;
import it.units.crossway.client.remote.StompMessageHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameHandlerTests {

    @Mock
    private Api api;
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void initWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(9111));
        wireMockServer.start();
    }

    @AfterEach
    void reset() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void shutdownWireMockServer() {
        wireMockServer.shutdown();
    }

    Api buildAndReturnFeignClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .target(Api.class, "http://localhost:9111");
    }

    @Test
    void whenStartGameShouldSetFirstTurnBlack() {
        Board board = new Board();
        Turn turn = new Turn();
        Player player = new Player("whiteP", PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.startGame();
        assertEquals(gameHandler.getTurn(), new Turn(1, PlayerColor.BLACK));
    }

    @Test
    void givenWhitePlayerWhenIsSecondTurnShouldAskForPieRule() {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        String input = "N" + System.lineSeparator() + "6,6" + System.lineSeparator();
        IOUtils.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        wireMockServer.stubFor(post(anyUrl()));
        gameHandler.playTurn();
        assertTrue(byteArrayOutputStream.toString().contains("Do you Want to switch colors? Y-yes N-No"));
    }

    @Test
    void givenWhitePlayerWhenIsSecondTurnAndPieRuleAcceptedPlayerAndTurnColorShouldBecomeBlack() {
        Api api = buildAndReturnFeignClient();
        wireMockServer.stubFor(post(anyUrl()));
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        board.placeStone(1, 4, PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        IOUtils.redirectScannerToSimulatedInput("Y" + System.lineSeparator());
        gameHandler.playTurn();
        assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getTurnColor());
        assertEquals(PlayerColor.BLACK, gameHandler.getPlayer().getColor());
    }

    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedPlayersShouldAskForStonePlacement() {
        Api api = buildAndReturnFeignClient();
        wireMockServer.stubFor(post(anyUrl()));
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("N" + System.lineSeparator() + "6,6" + System.lineSeparator());
        gameHandler.playTurn();
        assertTrue(byteArrayOutputStream.toString().contains("Insert a valid placement for your stone..."));
        assertEquals(PlayerColor.WHITE, gameHandler.getPlayer().getColor());
    }

    @Test
    void whenChooseNicknameShouldSendAddPlayerReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        PlayerDto playerDto = new PlayerDto(nickname);
        ObjectMapper om = new ObjectMapper();
        String jsonPlayerDto = om.writeValueAsString(playerDto);
        wireMockServer.stubFor(post(urlEqualTo("/players"))
                .withRequestBody(equalToJson(jsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonPlayerDto)));
        String input = nickname + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        IOUtils.scanner = new Scanner(System.in);
        gameHandler.chooseNickname();
        assertEquals(nickname, gameHandler.getPlayer().getNickname());
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/players"))
                .withRequestBody(equalToJson(jsonPlayerDto)));
    }

    @Test
    void whenPlayerSelectsNewGameShouldSendCreateGameReq() throws JsonProcessingException {
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
        String uuid = UUID.randomUUID().toString();
        GameDto gameDto = new GameDto(uuid, null, nickname);
        wireMockServer.stubFor(post(urlEqualTo("/games"))
                .withRequestBody(equalToJson(jsonGameCreationIntent))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto))));
        String createGameIntent = "1" + System.getProperty("line.separator");
        IOUtils.scanner = new Scanner(new ByteArrayInputStream(createGameIntent.getBytes()));
        gameHandler.chooseGameType();
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/games"))
                .withRequestBody(equalToJson(jsonGameCreationIntent)));
    }

    @Test
    void whenPlayerCreatesNewGameShouldSetUuidAndBlackPlayerColor() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getPlayer().setNickname(nickname);
        GameCreationIntent gameCreationIntent = new GameCreationIntent(nickname);
        ObjectMapper om = new ObjectMapper();
        String uuid = UUID.randomUUID().toString();
        GameDto gameDto = new GameDto(uuid, null, nickname);
        wireMockServer.stubFor(post(urlEqualTo("/games"))
                .withRequestBody(equalToJson(om.writeValueAsString(gameCreationIntent)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto))));
        String createGameIntent = "1" + System.getProperty("line.separator");
        IOUtils.scanner = new Scanner(new ByteArrayInputStream(createGameIntent.getBytes()));
        gameHandler.chooseGameType();
        assertEquals(uuid, gameHandler.getUuid());
        assertEquals(PlayerColor.BLACK, gameHandler.getPlayer().getColor());
    }

    @Test
    void whenPlayerSelectsJoinGameShouldSendGetAvailableGamesReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ObjectMapper om = new ObjectMapper();
        List<GameDto> availableGames = new ArrayList<>();
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        GameDto gameDto1 = new GameDto(uuid1, null, "blackP1");
        GameDto gameDto2 = new GameDto(uuid2, null, "blackP2");
        availableGames.add(gameDto1);
        availableGames.add(gameDto2);
        String jsonAvailableGames = om.writeValueAsString(availableGames);
        wireMockServer.stubFor(get(urlEqualTo("/games/available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonAvailableGames)));
        wireMockServer.stubFor(put(urlEqualTo("/games/" + uuid1))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = "2" + System.lineSeparator() + "0" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.chooseGameType();
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/games/available")));
    }

    @Test
    void whenPlayerSelectsJoinGameShouldListAvailableGames() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ObjectMapper om = new ObjectMapper();
        List<GameDto> availableGames = new ArrayList<>();
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        GameDto gameDto1 = new GameDto(uuid1, null, "blackP1");
        GameDto gameDto2 = new GameDto(uuid2, null, "blackP2");
        availableGames.add(gameDto1);
        availableGames.add(gameDto2);
        String jsonAvailableGames = om.writeValueAsString(availableGames);
        wireMockServer.stubFor(get(urlEqualTo("/games/available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonAvailableGames)));
        wireMockServer.stubFor(put(urlEqualTo("/games/" + uuid1))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = "2" + System.lineSeparator() + "0" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        gameHandler.chooseGameType();
        assertTrue(byteArrayOutputStream.toString().contains("opponent is blackP1"));
        assertTrue(byteArrayOutputStream.toString().contains("opponent is blackP2"));
    }

    @Test
    void whenPlayerSelectsAnAvailableGameShouldSendJoinGameReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ObjectMapper om = new ObjectMapper();
        List<GameDto> availableGames = new ArrayList<>();
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        GameDto gameDto1 = new GameDto(uuid1, null, "blackP1");
        GameDto gameDto2 = new GameDto(uuid2, null, "blackP2");
        availableGames.add(gameDto1);
        availableGames.add(gameDto2);
        String jsonAvailableGames = om.writeValueAsString(availableGames);
        wireMockServer.stubFor(get(urlEqualTo("/games/available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonAvailableGames)));
        wireMockServer.stubFor(put(urlEqualTo("/games/" + uuid1))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = "2" + System.lineSeparator() + "0" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.chooseGameType();
        wireMockServer.verify(1, putRequestedFor(urlEqualTo("/games/" + uuid1)));
    }

    @Test
    void whenPlayerJoinsAnAvailableGameShouldSetUuidAndWhitePlayerColor() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ObjectMapper om = new ObjectMapper();
        List<GameDto> availableGames = new ArrayList<>();
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        GameDto gameDto1 = new GameDto(uuid1, null, "blackP1");
        GameDto gameDto2 = new GameDto(uuid2, null, "blackP2");
        availableGames.add(gameDto1);
        availableGames.add(gameDto2);
        String jsonAvailableGames = om.writeValueAsString(availableGames);
        wireMockServer.stubFor(get(urlEqualTo("/games/available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonAvailableGames)));
        wireMockServer.stubFor(put(urlEqualTo("/games/" + uuid1))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = "2" + System.lineSeparator() + "0" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.chooseGameType();
        assertEquals(uuid1, gameHandler.getUuid());
        assertEquals(PlayerColor.WHITE, gameHandler.getPlayer().getColor());
    }

    @Test
    void whenStonePlacementIntentIsReceivedThenBoardShouldBeUpdated() {
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(3, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        StompMessageHandler stompMessageHandler = new StompMessageHandler(gameHandler);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent();
        stompMessageHandler.handleFrame(new StompHeaders(), stonePlacementIntent);
        assertEquals(PlayerColor.BLACK,
                gameHandler.getBoard().getStoneColorAt(stonePlacementIntent.getRow(), stonePlacementIntent.getColumn())
        );
    }

    @Test
    void whenStonePlacementIntentIsReceivedShouldGoToNextTurn() {
        Board board = new Board();
        Player player = new Player("playerW", PlayerColor.WHITE);
        Turn turn = new Turn(4, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        StompMessageHandler stompMessageHandler = new StompMessageHandler(gameHandler);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent();
        stompMessageHandler.handleFrame(new StompHeaders(), stonePlacementIntent);
        assertEquals(5, gameHandler.getTurn().getTurnNumber());
        assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getTurnColor());
    }

    @Test
    void givenBlackPlayerWhenGameStartsShouldPlayTurn() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(3, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        wireMockServer.stubFor(post(anyUrl()));
        gameHandler.startGame();
        assertTrue(byteArrayOutputStream.toString().contains("Insert a valid placement for your stone..."));
    }

    @Test
    void givenWhitePlayerWhenGameStartsShouldWaitForOpponentMove() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerW", PlayerColor.WHITE);
        Turn turn = new Turn(3, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        String uuid = UUID.randomUUID().toString();
        gameHandler.setUuid(uuid);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        gameHandler.startGame();
        assertTrue(byteArrayOutputStream.toString().contains("Waiting for opponent move..."));
    }

    @Test
    void whenPlayTurnShouldSendStonePlacementIntentReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(3, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        String uuid = UUID.randomUUID().toString();
        gameHandler.setUuid(uuid);
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        UrlPattern urlPattern = urlEqualTo("/games/" + uuid + "/play");
        StonePlacementIntentDto body = new StonePlacementIntentDto(6, 6, player.getNickname());
        String jsonBody = new ObjectMapper().writeValueAsString(body);
        wireMockServer.stubFor(post(urlPattern).withRequestBody(equalToJson(jsonBody)));
        gameHandler.startGame();
        wireMockServer.verify(1, postRequestedFor(urlPattern).withRequestBody(equalToJson(jsonBody)));
    }

    @Test
    void whenChooseNicknameAlreadyUsedShouldWarnAndAskForNickname() {
        Assertions.fail();
    }

    @Test
    void whenStonePlacementIntentIsNotValidShouldWarnAndAskForNewPlacement() {
        Assertions.fail();
    }

    @Test
    void whenOpponentJoinsShouldStartGameAndPlayFirstTurn() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(1, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        StompMessageHandler stompMessageHandler = new StompMessageHandler(gameHandler);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("join-event", "playerW");
        wireMockServer.stubFor(post(anyUrl()));
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        stompMessageHandler.handleFrame(stompHeaders, "");
        assertTrue(byteArrayOutputStream.toString().contains("Game start!!"));
        assertEquals(1, gameHandler.getTurn().getTurnNumber());
        assertTrue(byteArrayOutputStream.toString().contains("Insert a valid placement for your stone..."));
    }

    @Test
    void whenPlayerWinsShouldSendWinGameReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Board board = new Board();
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW; i++) {
            board.placeStone(i, 3, player.getColor());
        }
        Turn turn = new Turn(20, PlayerColor.BLACK);
        String uuid = UUID.randomUUID().toString();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.setUuid(uuid);
        PlayerDto playerDto = new PlayerDto(player.getNickname());
        ObjectMapper om = new ObjectMapper();
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid + "/play")));
        wireMockServer.stubFor(put(urlEqualTo("/games/" + uuid + "/win"))
                .withRequestBody(equalToJson(om.writeValueAsString(playerDto)))
                .willReturn(aResponse()
                        .withStatus(200)));
        IOUtils.redirectScannerToSimulatedInput(Board.LAST_ROW + ",4" + System.lineSeparator());
        gameHandler.playTurnIfSupposedTo();
        wireMockServer.verify(1, putRequestedFor(urlEqualTo("/games/" + uuid + "/win"))
                .withRequestBody(equalToJson(om.writeValueAsString(playerDto))));
    }

    @Test
    void whenWinGameEventIsReceivedShouldShowMessageAndEndGame() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(20, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        StompMessageHandler stompMessageHandler = new StompMessageHandler(gameHandler);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("win-event", "playerW");
        PrintStream out = System.out;
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        stompMessageHandler.handleFrame(stompHeaders, "");
        assertTrue(byteArrayOutputStream.toString().contains("You lose :(\nplayerW win"));
    }

}
