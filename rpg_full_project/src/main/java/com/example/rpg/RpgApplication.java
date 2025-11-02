package com.example.rpg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.rpg.model.CharacterClass;
import com.example.rpg.repository.CharacterClassRepository;

@SpringBootApplication
public class RpgApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpgApplication.class, args);
    }

    @Bean
    CommandLineRunner init(CharacterClassRepository classRepo) {
        return args -> {
            if (classRepo.count() == 0) {
                classRepo.save(new CharacterClass("Berserker", 120, 30, 8, 20, 0.75));
                classRepo.save(new CharacterClass("Guardiao", 140, 22, 20, 18, 0.85));
                classRepo.save(new CharacterClass("Elementalista", 100, 36, 6, 25, 0.70));
            }
        };
    }
}
