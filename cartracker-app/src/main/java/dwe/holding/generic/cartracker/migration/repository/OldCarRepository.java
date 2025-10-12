package dwe.holding.generic.cartracker.migration.repository;

import dwe.holding.generic.cartracker.migration.model.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface OldCarRepository extends JpaRepository<CarEntity, Long> {
    CarEntity findByName(String name);
}