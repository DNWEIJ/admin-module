package dwe.holding.generic.cartracker;


import dwe.holding.generic.cartracker.repository.CarRepository;
import dwe.holding.generic.cartracker.repository.DriveRepository;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {

    private final CarRepository carRepository;
    private final DriveRepository driveRepository;

    public MigrationService(CarRepository carRepository, DriveRepository driveRepository) {
        this.carRepository = carRepository;
        this.driveRepository = driveRepository;
    }

    public void init() {
        // no migration to execute
    }
}