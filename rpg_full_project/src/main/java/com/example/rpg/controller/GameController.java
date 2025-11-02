package com.example.rpg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.rpg.model.Game;
import com.example.rpg.service.GameService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) { this.gameService = gameService; }

    // listar classes
    @GetMapping("/classes")
    public ResponseEntity<?> listClasses() {
        return ResponseEntity.ok(gameService.listClasses());
    }

    public static class CreateGameRequest {
        @NotBlank
        private String playerName;
        @NotBlank
        private String chosenClass;
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public String getChosenClass() { return chosenClass; }
        public void setChosenClass(String chosenClass) { this.chosenClass = chosenClass; }
    }

    @PostMapping("/game")
    public ResponseEntity<?> createGame(@Valid @RequestBody CreateGameRequest req) {
        try {
            Game g = gameService.createGame(req.getPlayerName(), req.getChosenClass());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("gameId", g.getId(), "message", "Game created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public static class ActionRequest {
        private Long gameId;
        @NotBlank
        private String action;
        public Long getGameId() { return gameId; }
        public void setGameId(Long gameId) { this.gameId = gameId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }

    @PostMapping("/game/action")
    public ResponseEntity<?> action(@Valid @RequestBody ActionRequest req) {
        try {
            Game g = gameService.processTurn(req.getGameId(), req.getAction());
            return ResponseEntity.ok(g);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<?> getGame(@PathVariable Long id) {
        Optional<Game> g = gameService.findGame(id);
        return g.map(game -> ResponseEntity.ok(game)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Game) Map.of("error","Game not found")));
    }
}
