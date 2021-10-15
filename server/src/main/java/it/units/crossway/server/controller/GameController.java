package it.units.crossway.server.controller;

import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.GameJoinIntent;
import it.units.crossway.server.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameCreationIntent gameCreationIntent) {
        return ResponseEntity.ok(gameService.createGame(gameCreationIntent));
    }

    @PutMapping
    public ResponseEntity<GameDto> joinGame(@RequestBody GameJoinIntent gameJoinIntent) {
        return ResponseEntity.ok(gameService.joinGame(gameJoinIntent));
    }
}
