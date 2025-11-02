package com.example.rpg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rpg.model.CharacterClass;
import com.example.rpg.model.Game;
import com.example.rpg.repository.CharacterClassRepository;
import com.example.rpg.repository.GameRepository;

import java.util.*;

@Service
public class GameService {
    private final CharacterClassRepository classRepo;
    private final GameRepository gameRepo;
    private final Random rng = new Random();

    public GameService(CharacterClassRepository classRepo, GameRepository gameRepo) {
        this.classRepo = classRepo;
        this.gameRepo = gameRepo;
    }

    public List<CharacterClass> listClasses() {
        return classRepo.findAll();
    }

    @Transactional
    public Game createGame(String playerName, String chosenClassName) {
        CharacterClass playerClass = classRepo.findByName(chosenClassName)
                .orElseThrow(() -> new IllegalArgumentException("Classe não encontrada: " + chosenClassName));

        List<CharacterClass> all = classRepo.findAll();
        List<CharacterClass> possible = new ArrayList<>();
        for (CharacterClass c : all) {
            if (!c.getName().equals(playerClass.getName())) {
                possible.add(c);
            }
        }

        if (possible.isEmpty()) {
            throw new IllegalStateException("Não há classes suficientes para escolher inimigo.");
        }

        CharacterClass enemyClass = possible.get(rng.nextInt(possible.size()));

        Game g = new Game(playerName, playerClass, enemyClass);
        g.addAction(String.format("Game started: %s (%s) vs Computer (%s)",
                playerName, playerClass.getName(), enemyClass.getName()));
        return gameRepo.save(g);
    }

    @Transactional
    public Game processTurn(Long gameId, String playerAction) {
        Game g = gameRepo.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("Jogo não encontrado"));

        if (isFinished(g))
            throw new IllegalStateException("Jogo já finalizado");

        playerAction = playerAction.toUpperCase(Locale.ROOT);
        if (!playerAction.equals("ATTACK") && !playerAction.equals("HEAL"))
            throw new IllegalArgumentException("Ação inválida. Use ATTACK ou HEAL.");

        if (playerAction.equals("HEAL") && "HEAL".equals(g.getLastActionPlayer()))
            throw new IllegalArgumentException("Não é possível usar Curar-se duas vezes seguidas.");

        // Jogador
        g.setTurnNumber(g.getTurnNumber() + 1);
        String playerResult = applyAction(g.getPlayerClass(), g.getEnemyClass(), playerAction, true, g);
        g.setLastActionPlayer(playerAction);
        g.addAction(String.format("Turn %d: Player %s -> %s",
                g.getTurnNumber(), g.getPlayerClass().getName(), playerResult));

        if (isFinished(g)) {
            gameRepo.save(g);
            return g;
        }

        // Inimigo
        String enemyAction = chooseEnemyAction(g);
        if (enemyAction.equals("HEAL") && "HEAL".equals(g.getLastActionEnemy()))
            enemyAction = "ATTACK";

        String enemyResult = applyAction(g.getEnemyClass(), g.getPlayerClass(), enemyAction, false, g);
        g.setLastActionEnemy(enemyAction);
        g.addAction(String.format("Turn %d: Enemy %s -> %s",
                g.getTurnNumber(), g.getEnemyClass().getName(), enemyResult));

        gameRepo.save(g);
        return g;
    }

    private String chooseEnemyAction(Game g) {
        int enemyHp = g.getEnemyHp();
        int enemyMax = g.getEnemyClass().getMaxHp();
        double hpRatio = (double) enemyHp / (double) enemyMax;
        if (hpRatio <= 0.30 && !"HEAL".equals(g.getLastActionEnemy())) return "HEAL";
        return rng.nextDouble() < 0.6 ? "ATTACK" : "HEAL";
    }

    private String applyAction(CharacterClass actor, CharacterClass target, String action, boolean isPlayerActor, Game g) {
        action = action.toUpperCase(Locale.ROOT);

        if (action.equals("ATTACK")) {
            boolean success = rng.nextDouble() < actor.getSuccessRate();
            if (!success) return String.format("%s falhou o ataque!", actor.getName());

            int rawDamage = actor.getAttack() - target.getDefense();
            int damage = Math.max(0, rawDamage);

            if (isPlayerActor) {
                g.setEnemyHp(Math.max(0, g.getEnemyHp() - damage));
            } else {
                g.setPlayerHp(Math.max(0, g.getPlayerHp() - damage));
            }
            return String.format("%s atacou causando %d de dano!", actor.getName(), damage);

        } else if (action.equals("HEAL")) {
            boolean success = rng.nextDouble() < actor.getSuccessRate();
            if (!success) return String.format("%s tentou curar-se mas falhou!", actor.getName());

            int heal = actor.getHealAmount();
            if (isPlayerActor) {
                int newHp = Math.min(actor.getMaxHp(), g.getPlayerHp() + heal);
                int actual = newHp - g.getPlayerHp();
                g.setPlayerHp(newHp);
                return String.format("%s curou %d de vida!", actor.getName(), actual);
            } else {
                int newHp = Math.min(actor.getMaxHp(), g.getEnemyHp() + heal);
                int actual = newHp - g.getEnemyHp();
                g.setEnemyHp(newHp);
                return String.format("%s curou %d de vida!", actor.getName(), actual);
            }
        }

        return "Ação desconhecida.";
    }

    public boolean isFinished(Game g) {
        return g.getPlayerHp() <= 0 || g.getEnemyHp() <= 0;
    }

    public Optional<Game> findGame(Long id) {
        return gameRepo.findById(id);
    }
}
