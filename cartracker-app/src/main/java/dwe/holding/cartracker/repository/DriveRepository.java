package dwe.holding.cartracker.repository;

import dwe.holding.cartracker.model.Trip;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
  

@Repository
public interface DriveRepository extends JpaRepository<Trip,   Long> {

    List<Trip> findByPersonOrderByDriveDateAsc(String name);

    Trip findFirstByCarTypeOrderByKmTotalDesc(String carType);

    default List<Trip> findAllOrderByDriveDateAsc() {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "driveDate")
                .and(Sort.by(Sort.Direction.ASC, "id"));
        return findAll(sortBy);
    }
}