package dwe.holding.generic.teammover.repository;

import dwe.holding.generic.teammover.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.drivers " +
            "WHERE g.whenIsTheGame > :now AND g.localMemberId = :localMemberId " +
            "ORDER BY g.whenIsTheGame")
    List<Game> findGamesByWhenIsTheGameGreaterThanAndLocalMemberId(LocalDateTime now, UUID localMemberId);

    default List<Game> findGamesGreatherThenToday(UUID localMemberId) {
        return findGamesByWhenIsTheGameGreaterThanAndLocalMemberId(LocalDateTime.now().minusDays(1), localMemberId);
    }
}