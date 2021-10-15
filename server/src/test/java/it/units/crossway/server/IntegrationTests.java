package it.units.crossway.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameJoinIntent;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private GameRepository gameRepository;

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
    void when_postPlayerAndDuplicateNickname_then_401() {
        // todo
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
                .andExpect(jsonPath("$.blackPlayer", is("player1")));
    }

    @Test
    void given_previouslyCreatedGame_when_putGameJoiningIntent_should_setWhitePlayer() throws Exception {
        Game game = new Game();
        String uuid = UUID.randomUUID().toString();
        game.setUuid(uuid);
        game.setBlackPlayer("player1");
        game.setGameStatus(GameStatus.CREATED);
        gameRepository.save(game);
        GameJoinIntent gameJoinIntent = new GameJoinIntent(uuid, "player2");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(put("/games")
                        .content(om.writeValueAsString(gameJoinIntent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.whitePlayer", is("player2")));
    }

    @Test
    void when_putGameJoiningIntentAndGameDoesNotExists_then_401() {
        // todo
    }

}
