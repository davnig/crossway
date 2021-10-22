package it.units.crossway.server.service;

import it.units.crossway.server.exception.GameException;
import it.units.crossway.server.exception.GameNotFoundException;
import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.dto.StonePlacementIntent;
import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.GameStatus;
import it.units.crossway.server.repository.GameRepository;
import it.units.crossway.server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameService(GameRepository gameRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameRepository = gameRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public GameDto getGameByUuid(String uuid) {
        return new GameDto(gameRepository.findByUuid(uuid)
                .orElseThrow(() -> new GameNotFoundException("Game with {uuid = " + uuid + "} not found")));
    }

    public List<GameDto> getAllAvailableGames() {
        return gameRepository.findAll().stream()
                .filter(game -> game.getGameStatus().equals(GameStatus.CREATED))
                .map(GameDto::new)
                .collect(Collectors.toList());
    }

    public GameDto createGame(GameCreationIntent intent) {
        Game game = new Game();
        game.setUuid(UUID.randomUUID().toString());
        game.setBlackPlayerNickname(intent.getPlayerNickname());
        game.setGameStatus(GameStatus.CREATED);
        return new GameDto(gameRepository.save(game));
    }

    public GameDto joinGame(String uuid, PlayerDto playerDto) {
        Game gameToJoin = gameRepository.findByUuid(uuid)
                .orElseThrow(() -> new GameException("The game does not exist"));
        checkIfGameIsJoinable(gameToJoin);
        gameToJoin.setWhitePlayerNickname(playerDto.getNickname());
        gameToJoin.setGameStatus(GameStatus.IN_PROGRESS);
        return new GameDto(gameToJoin);
    }

    @Transactional
    public void deleteGame(String uuid) {
        if (!gameRepository.existsByUuid(uuid))
            throw new GameNotFoundException("Game with {uuid = " + uuid + "} not found");
        gameRepository.deleteByUuid(uuid);
    }

    private void checkIfGameIsJoinable(Game gameToJoin) {
        if (gameToJoin.getGameStatus() != GameStatus.CREATED || gameToJoin.getWhitePlayerNickname() != null) {
            throw new GameException("The game is not valid anymore");
        }
    }

    public void placeStone(String uuid, StonePlacementIntent stonePlacementIntent) {
        validateStonePlacementIntent(uuid, stonePlacementIntent);
        simpMessagingTemplate.convertAndSend("/topic/" + uuid, stonePlacementIntent);
    }

    private void validateStonePlacementIntent(String uuid, StonePlacementIntent stonePlacementIntent) {
        String nickname = stonePlacementIntent.getNickname();
        Game game = gameRepository.findByUuid(uuid)
                .orElseThrow(() -> new GameException("The game does not exist"));
        checkIfGameIsInProgress(game);
        checkIfNicknameExists(nickname);
        checkIfNicknameBelongsToGame(nickname, game);
    }

    private void checkIfGameIsInProgress(Game game) {
        if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new GameException("The game is ended");
        }
    }

    private void checkIfNicknameExists(String nickname) {
        if (!playerRepository.existsByNickname(nickname)) {
            throw new GameException("The player with {nickname = " + nickname + "} does not exist");
        }
    }

    private void checkIfNicknameBelongsToGame(String nickname, Game game) {
        if (!Objects.equals(game.getWhitePlayerNickname(), nickname) && !Objects.equals(game.getBlackPlayerNickname(), nickname)) {
            throw new GameException("The player with {nickname = " + nickname + "} does belong to this game");
        }
    }


    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
