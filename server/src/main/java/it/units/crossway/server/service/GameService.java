package it.units.crossway.server.service;

import it.units.crossway.server.exception.GameException;
import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.GameJoinIntent;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameDto createGame(GameCreationIntent intent) {
        Game game = new Game();
        game.setUuid(UUID.randomUUID().toString());
        game.setBlackPlayer(intent.getPlayerNickname());
        return new GameDto(gameRepository.save(game));
    }

    public GameDto joinGame(GameJoinIntent gameJoinIntent) {
        Game gameToJoin = gameRepository.findByUuid(gameJoinIntent.getUuid())
                .orElseThrow(() -> new GameException("The game does not exist"));
        if (gameToJoin.getWhitePlayer() != null) {
            throw new GameException("The game is not valid anymore");
        }
        gameToJoin.setWhitePlayer(gameJoinIntent.getPlayerNickname());
        return new GameDto(gameToJoin);
    }
}
