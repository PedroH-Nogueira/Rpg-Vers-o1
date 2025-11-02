package com.example.rpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rpg.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {}
