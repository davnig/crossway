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

    @PostMapping("/{uuid}/events/joining")
    public ResponseEntity<GameDto> handleJoiningEvent(@PathVariable String uuid, @RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(gameService.handleJoiningEvent(uuid, playerDto));
    }

    @PostMapping("/{uuid}/events/placement")
    public ResponseEntity<Void> handlePlacementEvent(@PathVariable String uuid, @RequestBody StonePlacementIntent stonePlacementIntent) {
        gameService.handlePlacementEvent(uuid, stonePlacementIntent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uuid}/events/win")
    public ResponseEntity<Void> handleWinEvent(@PathVariable String uuid, @RequestBody PlayerDto playerDto) {
        gameService.handleWinEvent(uuid, playerDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uuid}/events/pie-rule")
    public ResponseEntity<Void> handlePieRuleEvent(@PathVariable String uuid) {
        gameService.handlePieRuleEvent(uuid);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteGame(@PathVariable String uuid) {
        gameService.deleteGame(uuid);
        return ResponseEntity.ok().build();
    }

}
