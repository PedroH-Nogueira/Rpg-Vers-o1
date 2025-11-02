RPG Full Project - Spring Boot (API + Persistence)

Como usar:
1. Abrir no IntelliJ como projeto Maven.
2. Rodar `com.example.rpg.RpgApplication`.
3. Endpoints:
   - GET /api/classes -> lista classes
   - POST /api/game -> cria jogo { "playerName":"Pedro", "chosenClass":"Berserker" }
   - POST /api/game/action -> realiza ação { "gameId":1, "action":"ATTACK" }
   - GET /api/game/{id} -> estado do jogo
H2 console: http://localhost:8080/h2-console (jdbc url já está configurada)
