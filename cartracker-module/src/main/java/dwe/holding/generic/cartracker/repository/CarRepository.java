package dwe.holding.generic.cartracker.repository;


import dwe.holding.generic.cartracker.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    Car findByName(String name);
}