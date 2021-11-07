package it.units.crossway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import it.units.crossway.client.exception.InvalidUserInputException;
import it.units.crossway.client.model.*;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.model.dto.StonePlacementIntentDto;
import it.units.crossway.client.remote.Api;
import it.units.crossway.client.remote.StompMessageHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static it.units.crossway.client.IOUtils.JOIN_GAME_CHOICE;
import static it.units.crossway.client.IOUtils.NEW_GAME_CHOICE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Frame.class)
public class GameHandlerTests {

    @Mock
    private Api api;
    @Autowired
    private Frame frame;
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void initWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(9111));
        wireMockServer.start();
    }

    @AfterEach
    void reset() {
        wireMockServer.resetAll();
        Rules.isPieRuleAccepted = false;
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        gameHandler.startGame();
        assertEquals(gameHandler.getTurn(), new Turn(1, PlayerColor.BLACK));
    }

    @Test
    void givenWhitePlayerWhenIsSecondTurnShouldAskForPieRule() {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        String input = "N" + System.lineSeparator() + "6,6" + System.lineSeparator();
        IOUtils.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        wireMockServer.stubFor(post(anyUrl()));
        gameHandler.playTurn();
        assertTrue(byteArrayOutputStream.toString().contains("Do you want to claim the pie rule? Y-yes N-No"));
    }

    @Test
    void whenIsSecondTurnAndPieRuleAcceptedThenPlayerShouldBecomeBlackAndTurnColorStillWhite() {
        Api api = buildAndReturnFeignClient();
        wireMockServer.stubFor(post(anyUrl()));
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        board.placeStone(1, 4, PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        IOUtils.redirectScannerToSimulatedInput("Y" + System.lineSeparator());
        gameHandler.playTurn();
        assertEquals(PlayerColor.WHITE, gameHandler.getTurn().getTurnColor());
        assertEquals(PlayerColor.BLACK, gameHandler.getPlayer().getColor());
        assertEquals(2, gameHandler.getTurn().getTurnNumber());
    }

    @Test
    void givenWhitePlayerWhenIsSecondTurnAndPieRuleAcceptedShouldSendPieRuleReq() {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        Turn turn = new Turn(2, PlayerColor.WHITE);
        String uuid = UUID.randomUUID().toString();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        gameHandler.setUuid(uuid);
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid + "/events/pie-rule")));
        IOUtils.redirectScannerToSimulatedInput("Y" + System.lineSeparator());
        gameHandler.playTurn();
        wireMockServer.verify(1, postRequestedFor(
                urlEqualTo("/games/" + uuid + "/events/pie-rule")));
    }

    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedPlayersShouldAskForStonePlacement() {
        Api api = buildAndReturnFeignClient();
        wireMockServer.stubFor(post(anyUrl()));
        Player player = new Player("whiteP", PlayerColor.WHITE);
        Board board = new Board();
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("N" + System.lineSeparator() + "6,6" + System.lineSeparator());
        gameHandler.playTurn();
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.IO_INSERT_VALID_PLACEMENT));
        assertEquals(PlayerColor.WHITE, gameHandler.getPlayer().getColor());
    }

    @Test
    void whenChooseNicknameShouldSendAddPlayerReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        String createGameIntent = NEW_GAME_CHOICE + System.getProperty("line.separator");
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        String createGameIntent = NEW_GAME_CHOICE + System.getProperty("line.separator");
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid1 + "/events/joining"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = JOIN_GAME_CHOICE + System.lineSeparator() + "1" + System.lineSeparator();
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid1 + "/events/joining"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = JOIN_GAME_CHOICE + System.lineSeparator() + "1" + System.lineSeparator();
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid1 + "/events/joining"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = JOIN_GAME_CHOICE + System.lineSeparator() + "1" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.chooseGameType();
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/games/" + uuid1 + "/events/joining")));
    }

    @Test
    void whenPlayerJoinsAnAvailableGameShouldSetUuidAndWhitePlayerColor() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
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
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid1 + "/events/joining"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(om.writeValueAsString(gameDto1))));
        String input = JOIN_GAME_CHOICE + System.lineSeparator() + "1" + System.lineSeparator();
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        StompMessageHandler stompMessageHandler = new StompMessageHandler();
        stompMessageHandler.setPlacementEventListener(gameHandler);
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
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        StompMessageHandler stompMessageHandler = new StompMessageHandler();
        stompMessageHandler.setPlacementEventListener(gameHandler);
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
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        wireMockServer.stubFor(post(anyUrl()));
        gameHandler.startGame();
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.IO_INSERT_VALID_PLACEMENT));
    }

    @Test
    void givenWhitePlayerWhenGameStartsShouldWaitForOpponentMove() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerW", PlayerColor.WHITE);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        String uuid = UUID.randomUUID().toString();
        gameHandler.setUuid(uuid);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        gameHandler.startGame();
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.IO_WAITING_FOR_OPPONENT_MOVE));
    }

    @Test
    void whenPlayTurnShouldSendStonePlacementIntentReq() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        String uuid = UUID.randomUUID().toString();
        gameHandler.setUuid(uuid);
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        UrlPattern urlPattern = urlEqualTo("/games/" + uuid + "/events/placement");
        StonePlacementIntentDto body = new StonePlacementIntentDto(6, 6, player.getNickname());
        String jsonBody = new ObjectMapper().writeValueAsString(body);
        wireMockServer.stubFor(post(urlPattern).withRequestBody(equalToJson(jsonBody)));
        gameHandler.startGame();
        wireMockServer.verify(1, postRequestedFor(urlPattern).withRequestBody(equalToJson(jsonBody)));
    }

    @Test
    void whenChooseNicknameAlreadyUsedShouldWarnAndAskForNewNickname() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        String nickname = "playerXZX";
        String newNickname = "abc";
        Player player = new Player(nickname, null);
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        PlayerDto playerDto = new PlayerDto(nickname);
        PlayerDto NewPlayerDto = new PlayerDto(newNickname);
        ObjectMapper om = new ObjectMapper();
        String jsonPlayerDto = om.writeValueAsString(playerDto);
        String newJsonPlayerDto = om.writeValueAsString(NewPlayerDto);
        UrlPattern urlPattern = urlEqualTo("/players");
        wireMockServer.stubFor(post(urlPattern).inScenario("fail/success scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .withRequestBody(equalToJson(jsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(400)).willSetStateTo("success"));
        wireMockServer.stubFor(post(urlPattern).inScenario("fail/success scenario")
                .whenScenarioStateIs("success")
                .withRequestBody(equalToJson(newJsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(200)));
        String input = nickname + System.lineSeparator() + newNickname + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.chooseNickname();
        wireMockServer.verify(1, postRequestedFor(urlPattern).withRequestBody(equalToJson(newJsonPlayerDto)));
    }

    @Test
    void whenStonePlacementIntentIsNotValidShouldWarnAndAskForNewStonePlacement() throws JsonProcessingException {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(3, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        String uuid = UUID.randomUUID().toString();
        gameHandler.setUuid(uuid);
        UrlPattern urlPattern = urlEqualTo("/games/" + uuid + "/events/placement");
        StonePlacementIntentDto placementDto = new StonePlacementIntentDto(-6, 100, player.getNickname());
        String jsonBody = new ObjectMapper().writeValueAsString(placementDto);
        StonePlacementIntentDto newPlacementDto = new StonePlacementIntentDto(10, 1, player.getNickname());
        String newJsonBody = new ObjectMapper().writeValueAsString(newPlacementDto);
        wireMockServer.stubFor(post(urlPattern).inScenario("fail/success scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .withRequestBody(equalToJson(jsonBody))
                .willReturn(aResponse()
                        .withStatus(400)).willSetStateTo("success"));
        wireMockServer.stubFor(post(urlPattern).inScenario("fail/success scenario")
                .whenScenarioStateIs("success")
                .withRequestBody(equalToJson(newJsonBody))
                .willReturn(aResponse()
                        .withStatus(200)));
        String input = "-6,100" + System.lineSeparator() + "10,1" + System.lineSeparator();
        IOUtils.redirectScannerToSimulatedInput(input);
        gameHandler.playTurn();
        wireMockServer.verify(1, postRequestedFor(urlPattern).withRequestBody(equalToJson(newJsonBody)));
    }

    @Test
    void whenOpponentJoinsShouldStartGameAndPlayFirstTurn() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        StompMessageHandler stompMessageHandler = new StompMessageHandler();
        stompMessageHandler.setJoinEventListener(gameHandler);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("join-event", "playerW");
        wireMockServer.stubFor(post(anyUrl()));
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        stompMessageHandler.handleFrame(stompHeaders, "");
        assertTrue(byteArrayOutputStream.toString().contains("Game start!!"));
        assertEquals(1, gameHandler.getTurn().getTurnNumber());
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.IO_INSERT_VALID_PLACEMENT));
    }

    @Test
    void whenPlayerWinsShouldSendWinGameReq() throws Exception {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Board board = new Board();
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW; i++) {
            board.placeStone(i, 3, player.getColor());
        }
        Turn turn = new Turn(20, PlayerColor.BLACK);
        String uuid = UUID.randomUUID().toString();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        gameHandler.setUuid(uuid);
        PlayerDto playerDto = new PlayerDto(player.getNickname());
        ObjectMapper om = new ObjectMapper();
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid + "/play")));
        wireMockServer.stubFor(post(urlEqualTo("/games/" + uuid + "/events/win"))
                .withRequestBody(equalToJson(om.writeValueAsString(playerDto)))
                .willReturn(aResponse()
                        .withStatus(200)));
        IOUtils.redirectScannerToSimulatedInput(Board.LAST_ROW + ",4" + System.lineSeparator());
        gameHandler.playTurnIfSupposedTo();
        SystemLambda.catchSystemExit(gameHandler::endTurn);
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/games/" + uuid + "/events/win"))
                .withRequestBody(equalToJson(om.writeValueAsString(playerDto))));
    }

    @Test
    void givenWinningBoardForPlayerWhenEndTurnShouldShowWinMessageAndEndGame() throws Exception {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Board board = new Board();
        IntStream.range(Board.FIRST_ROW, Board.LAST_ROW + 1)
                .forEach(row -> board.placeStone(row, 5, player.getColor()));
        Turn turn = new Turn(20, player.getColor());
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        SystemLambda.catchSystemExit(gameHandler::endTurn);
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.WIN_MESSAGE));
    }

    @Test
    void givenWinningBoardForOpponentWhenEndTurnShouldShowLoseMessageAndEndGame() throws Exception {
        Api api = buildAndReturnFeignClient();
        Player player = new Player("playerW", PlayerColor.WHITE);
        Board board = new Board();
        IntStream.range(Board.FIRST_ROW, Board.LAST_ROW + 1)
                .forEach(row -> board.placeStone(row, 5, player.getColor().getOpposite()));
        Turn turn = new Turn(20, player.getColor().getOpposite());
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        SystemLambda.catchSystemExit(gameHandler::endTurn);
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.LOSE_MESSAGE));
    }

    @Test
    void whenPieRuleEventIsReceivedThenBlackPlayerShouldApplyPieRuleAndPlaySecondTurnAsWhite() {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerB", PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        wireMockServer.stubFor(post(anyUrl()));
        ByteArrayOutputStream byteArrayOutputStream = IOUtils.redirectSystemOutToByteArrayOS();
        IOUtils.redirectScannerToSimulatedInput("6,6" + System.lineSeparator());
        StompMessageHandler stompMessageHandler = new StompMessageHandler();
        stompMessageHandler.setPieRuleEventListener(gameHandler);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("pie-rule-event", "true");
        stompMessageHandler.handleFrame(stompHeaders, "");
        assertEquals(2, gameHandler.getTurn().getTurnNumber());
        assertEquals(PlayerColor.WHITE, gameHandler.getTurn().getTurnColor());
        assertTrue(byteArrayOutputStream.toString().contains(IOUtils.IO_INSERT_VALID_PLACEMENT));
    }

    @ParameterizedTest
    @ValueSource(strings = {".6,6", "4,4,", "  5.6", "", "  ", "...", ",", ".,.", "1000", ",45"})
    void whenInvalidUserInputStonePlacementShouldThrowInvalidUserInputException(String input) {
        Player player = new Player("player", PlayerColor.WHITE);
        IOUtils.redirectScannerToSimulatedInput(input + System.lineSeparator());
        assertThrows(InvalidUserInputException.class, () -> IOUtils.getStonePlacementIntentFromInput(player));
    }

    @Test
    void whenPlayerChooseGameTypeAndQuitShouldSendDeletePlayerRequest() throws Exception {
        Api api = buildAndReturnFeignClient();
        Board board = new Board();
        Player player = new Player("playerXYZ", PlayerColor.BLACK);
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api, frame);
        UrlPattern urlPattern = urlEqualTo("/players");
        PlayerDto playerDto = new PlayerDto(player.getNickname());
        ObjectMapper om = new ObjectMapper();
        String jsonPlayerDto = om.writeValueAsString(playerDto);
        wireMockServer.stubFor(post(urlPattern)
                .withRequestBody(equalToJson(jsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonPlayerDto)));
        wireMockServer.stubFor(delete(urlPattern)
                .withRequestBody(equalToJson(jsonPlayerDto))
                .willReturn(aResponse()
                        .withStatus(200)));
        IOUtils.redirectScannerToSimulatedInput(player.getNickname() + System.lineSeparator());
        gameHandler.chooseNickname();
        IOUtils.redirectScannerToSimulatedInput(IOUtils.QUIT_GAME_CHOICE + System.lineSeparator());
        SystemLambda.catchSystemExit(gameHandler::chooseGameType);
        wireMockServer.verify(1, deleteRequestedFor(urlPattern).withRequestBody(equalToJson(jsonPlayerDto)));
    }

}
