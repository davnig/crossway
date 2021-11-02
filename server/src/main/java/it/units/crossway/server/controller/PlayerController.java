package it.units.crossway.server.controller;

import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerDto> addPlayer(@RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(playerService.addPlayer(playerDto));
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> deletePlayerByNickname(@PathVariable String nickname) {
        playerService.deletePlayerByNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<PlayerDto> getPlayerByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(playerService.getPlayerByNickname(nickname));
    }

}
