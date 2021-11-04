package it.units.crossway.server.service;

import it.units.crossway.server.exception.DuplicatePlayerException;
import it.units.crossway.server.exception.PlayerNotFoundException;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerDto addPlayer(PlayerDto playerDto) {
        if (playerRepository.existsByNickname(playerDto.getNickname())) {
            throw new DuplicatePlayerException("nickname", playerDto.getNickname());
        }
        Player saved = playerRepository.save(new Player(playerDto.getNickname()));
        return new PlayerDto(saved.getNickname());
    }

    @Transactional
    public void deletePlayerByNickname(String nickname) {
        if (!playerRepository.existsByNickname(nickname)) {
            throw new PlayerNotFoundException("Player with {nickname = " + nickname + "} not found");
        }
        playerRepository.deleteByNickname(nickname);
    }

    public PlayerDto getPlayerByNickname(String nickname) {
        return new PlayerDto(playerRepository.findByNickname(nickname).orElseThrow(() -> new PlayerNotFoundException("Player with {nickname = " + nickname + "} not found")));
    }

}
