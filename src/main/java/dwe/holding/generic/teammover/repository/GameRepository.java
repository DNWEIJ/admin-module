package dwe.holding.generic.teammover.repository;

import dwe.holding.generic.teammover.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}