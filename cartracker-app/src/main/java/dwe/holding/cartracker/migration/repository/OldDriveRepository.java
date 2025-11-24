package dwe.holding.cartracker.migration.repository;

import dwe.holding.cartracker.migration.model.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Deprecated
public interface OldDriveRepository extends JpaRepository<TripEntity, UUID> {

}