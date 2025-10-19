package dwe.holding.cartracker.migration.repository;

import dwe.holding.cartracker.migration.model.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Deprecated
public interface OldCarRepository extends JpaRepository<CarEntity, UUID> {

}