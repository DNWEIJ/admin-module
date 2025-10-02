package dwe.holding.generic.app.teammover.repository;

import dwe.holding.generic.app.teammover.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.drivers WHERE g.whenIsTheGame > :now ORDER BY g.whenIsTheGame")
    List<Game> findGamesByWhenIsTheGameGreaterThan(LocalDateTime now);


    default List<Game> findGamesGreatherThenToday() {
        return findGamesByWhenIsTheGameGreaterThan(LocalDateTime.now().minusDays(1));
    }
}