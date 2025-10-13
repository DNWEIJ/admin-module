package dwe.holding.cartracker.migration.repository;

import dwe.holding.cartracker.migration.model.TripEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface OldDriveRepository extends JpaRepository<TripEntity, Long> {

    List<TripEntity> findByPersonOrderByDriveDateAsc(String name);

    TripEntity findFirstByCarTypeOrderByKmTotalDesc(String carType);

    default List<TripEntity> findAllOrderByDriveDateAsc() {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "driveDate")
                .and(Sort.by(Sort.Direction.ASC, "id"));
        return findAll(sortBy);
    }
}