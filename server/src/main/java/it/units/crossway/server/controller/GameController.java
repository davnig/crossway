package it.units.crossway.server.controller;

import it.units.crossway.server.model.dto.GameCreationIntent;
import it.units.crossway.server.model.dto.GameDto;
import it.units.crossway.server.model.dto.PlayerDto;
import it.units.crossway.server.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameCreationIntent gameCreationIntent) {
        return ResponseEntity.ok(gameService.createGame(gameCreationIntent));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<GameDto> joinGame(@PathVariable String uuid, @RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(gameService.joinGame(uuid, playerDto));
    }

//    @PutMapping("/{uuid}/play")
//    public ResponseEntity<String> playTurn(@PathVariable String uuid, @RequestBody PlayTurnIntent playTurnIntent) {
//        simpMessagingTemplate.convertAndSend("/topic/greetings", "ok");
//        return ResponseEntity.ok().build();
//    }

    @MessageMapping("/greetings")
    public String greeting(String payload) {
        return "ok";
    }

}
