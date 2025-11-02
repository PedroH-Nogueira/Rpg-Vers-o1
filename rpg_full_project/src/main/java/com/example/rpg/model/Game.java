package com.example.rpg.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;

    @ManyToOne
    private CharacterClass playerClass;

    @ManyToOne
    private CharacterClass enemyClass;

    private int playerHp;
    private int enemyHp;
    private int turnNumber;

    private String lastActionPlayer;
    private String lastActionEnemy;

    @ElementCollection
    @CollectionTable(name = "game_actions", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "action")
    private List<String> actions = new ArrayList<>();

    public Game() {}

    public Game(String playerName, CharacterClass playerClass, CharacterClass enemyClass) {
        this.playerName = playerName;
        this.playerClass = playerClass;
        this.enemyClass = enemyClass;
        this.playerHp = playerClass.getMaxHp();
        this.enemyHp = enemyClass.getMaxHp();
        this.turnNumber = 0;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public CharacterClass getPlayerClass() { return playerClass; }
    public void setPlayerClass(CharacterClass playerClass) { this.playerClass = playerClass; }
    public CharacterClass getEnemyClass() { return enemyClass; }
    public void setEnemyClass(CharacterClass enemyClass) { this.enemyClass = enemyClass; }
    public int getPlayerHp() { return playerHp; }
    public void setPlayerHp(int playerHp) { this.playerHp = playerHp; }
    public int getEnemyHp() { return enemyHp; }
    public void setEnemyHp(int enemyHp) { this.enemyHp = enemyHp; }
    public int getTurnNumber() { return turnNumber; }
    public void setTurnNumber(int turnNumber) { this.turnNumber = turnNumber; }
    public String getLastActionPlayer() { return lastActionPlayer; }
    public void setLastActionPlayer(String lastActionPlayer) { this.lastActionPlayer = lastActionPlayer; }
    public String getLastActionEnemy() { return lastActionEnemy; }
    public void setLastActionEnemy(String lastActionEnemy) { this.lastActionEnemy = lastActionEnemy; }
    public List<String> getActions() { return actions; }
    public void setActions(List<String> actions) { this.actions = actions; }
    public void addAction(String action) { this.actions.add(action); }
}
