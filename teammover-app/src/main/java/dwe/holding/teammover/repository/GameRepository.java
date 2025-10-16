package dwe.holding.teammover.repository;

import dwe.holding.teammover.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
  

public interface GameRepository extends JpaRepository<Game,   Long> {

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.drivers " +
            "WHERE g.whenIsTheGame > :now AND g.localMemberId = :localMemberId " +
            "ORDER BY g.whenIsTheGame")
    List<Game> findGamesByWhenIsTheGameGreaterThanAndLocalMemberId(LocalDateTime now,   Long localMemberId);

    default List<Game> findGamesGreatherThenToday(  Long localMemberId) {
        return findGamesByWhenIsTheGameGreaterThanAndLocalMemberId(LocalDateTime.now().minusDays(1), localMemberId);
    }
}