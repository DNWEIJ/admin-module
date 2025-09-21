package dwe.holding.generic.teammover.repository;

import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.teammover.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}