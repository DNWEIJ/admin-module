package dwe.holding.generic.teammover.repository;

import dwe.holding.generic.teammover.model.Game;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findGamesByWhenIsTheGameGreaterThan(LocalDateTime now, Sort whenIsTheGame);

    default List<Game> findGamesGreatherThenToday(Sort whenIsTheGame) {
        return findGamesByWhenIsTheGameGreaterThan(LocalDateTime.now().minusDays(1), whenIsTheGame);
    }
}