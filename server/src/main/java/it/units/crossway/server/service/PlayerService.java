package it.units.crossway.server.service;

import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerDto addUser(PlayerDto playerDto) {
        Player saved = playerRepository.save(new Player(playerDto.getNickname()));
        return new PlayerDto(saved.getNickname());
    }

}
