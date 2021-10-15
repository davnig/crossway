package it.units.crossway.server.service;

import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.entity.Player;
import it.units.crossway.server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final UserRepository userRepository;

    public PlayerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PlayerDto addUser(PlayerDto playerDto) {
        Player saved = userRepository.save(new Player(playerDto.getNickname()));
        return new PlayerDto(saved.getNickname());
    }

}
