package dwe.holding.generic.cartracker.repository;


import dwe.holding.generic.cartracker.model.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {
    CarEntity findByName(String name);
}