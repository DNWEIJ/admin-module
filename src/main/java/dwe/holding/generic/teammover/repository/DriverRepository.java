package dwe.holding.generic.teammover.repository;

import dwe.holding.generic.teammover.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
}