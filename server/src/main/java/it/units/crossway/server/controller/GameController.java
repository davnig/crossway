package it.units.crossway.server.controller;

import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.model.dto.StonePlacementIntent;
import it.units.crossway.server.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GameDto> getGameByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(gameService.getGameByUuid(uuid));
    }

    @GetMapping("/available")
    public ResponseEntity<List<GameDto>> getAllAvailableGames() {
        return ResponseEntity.ok(gameService.getAllAvailableGames());
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameCreationIntent gameCreationIntent) {
        return ResponseEntity.ok(gameService.createGame(gameCreationIntent));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<GameDto> joinGame(@PathVariable String uuid, @RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(gameService.joinGame(uuid, playerDto));
    }

    @PostMapping("/{uuid}/play")
    public ResponseEntity<Void> placeStone(@PathVariable String uuid, @RequestBody StonePlacementIntent stonePlacementIntent) {
        gameService.placeStone(uuid, stonePlacementIntent);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}/win")
    public ResponseEntity<Void> winGame(@PathVariable String uuid, @RequestBody PlayerDto playerDto) {
        gameService.winGame(uuid, playerDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteGame(@PathVariable String uuid) {
        gameService.deleteGame(uuid);
        return ResponseEntity.ok().build();
    }

}
