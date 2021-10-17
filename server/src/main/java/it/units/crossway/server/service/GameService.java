package it.units.crossway.server.service;

import it.units.crossway.server.exception.GameException;
import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.dto.StonePlacementIntent;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.repository.GameRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameService(GameRepository gameRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameRepository = gameRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public GameDto createGame(GameCreationIntent intent) {
        Game game = new Game();
        game.setUuid(UUID.randomUUID().toString());
        game.setBlackPlayer(intent.getPlayerNickname());
        game.setGameStatus(GameStatus.CREATED);
        return new GameDto(gameRepository.save(game));
    }

    public GameDto joinGame(String uuid, PlayerDto playerDto) {
        Game gameToJoin = gameRepository.findByUuid(uuid)
                .orElseThrow(() -> new GameException("The game does not exist"));
        if (gameToJoin.getGameStatus() != GameStatus.CREATED || gameToJoin.getWhitePlayer() != null) {
            throw new GameException("The game is not valid anymore");
        }
        gameToJoin.setWhitePlayer(playerDto.getNickname());
        gameToJoin.setGameStatus(GameStatus.IN_PROGRESS);
        return new GameDto(gameToJoin);
    }

    public void placeStone(String uuid, StonePlacementIntent stonePlacementIntent) {
        Game game = gameRepository.findByUuid(uuid)
                .orElseThrow(() -> new GameException("The game does not exist"));
        if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new GameException("The game is not valid anymore");
        }
        simpMessagingTemplate.convertAndSend("/topic/" + uuid, stonePlacementIntent);
    }
}
