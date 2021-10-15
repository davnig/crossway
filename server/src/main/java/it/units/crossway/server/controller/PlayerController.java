package it.units.crossway.server.controller;

import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerDto> addPlayer(@RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(playerService.addUser(playerDto));
    }

}
