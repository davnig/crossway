package it.units.crossway.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.GameRepository;
import it.units.crossway.server.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void when_postPlayer_should_saveInDb() throws Exception {
        PlayerDto user = new PlayerDto("player1");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(post("/players")
                        .content(om.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.nickname", is("player1")));
    }

    @Test
    void when_postPlayerAndDuplicateNickname_then_401() throws Exception {
        Player player1 = new Player("playerX");
        playerRepository.save(player1);
        PlayerDto user = new PlayerDto("playerX");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(post("/players")
                        .content(om.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_postGameCreationIntent_should_createNewGameWithUuidAndBlackPlayerSet() throws Exception {
        GameCreationIntent gameCreationIntent = new GameCreationIntent("player1");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(post("/games")
                        .content(om.writeValueAsString(gameCreationIntent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.uuid", matchesRegex("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b")))
                .andExpect(jsonPath("$.blackPlayerNickname", is("player1")));
    }

    @Test
    void given_previouslyCreatedGame_when_putGameJoiningIntent_should_setWhitePlayer() throws Exception {
        Game game = new Game();
        String uuid = UUID.randomUUID().toString();
        game.setUuid(uuid);
        game.setBlackPlayerNickname("player1");
        game.setGameStatus(GameStatus.CREATED);
        gameRepository.save(game);
        PlayerDto player2 = new PlayerDto("player2");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(put("/games/{uuid}", uuid)
                        .content(om.writeValueAsString(player2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.whitePlayerNickname", is("player2")));
    }

    @Test
    void when_putGameJoiningIntentAndGameDoesNotExists_then_401() throws Exception {
        String uuid = "fake-uuid";
        PlayerDto player0 = new PlayerDto("player0");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(put("/games/{uuid}", uuid)
                        .content(om.writeValueAsString(player0))
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_getAvailableGames_then_200() throws Exception {
        Game game = new Game();
        game.setUuid(UUID.randomUUID().toString());
        game.setWhitePlayerNickname("whiteP");
        game.setBlackPlayerNickname("blackP");
        game.setGameStatus(GameStatus.CREATED);
        gameRepository.save(game);
        mvc.perform(get("/games/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].whitePlayerNickname", is("whiteP")));
    }

}
