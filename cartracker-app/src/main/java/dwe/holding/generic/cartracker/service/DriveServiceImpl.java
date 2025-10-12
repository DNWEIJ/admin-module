package dwe.holding.generic.cartracker.service;


import dwe.holding.generic.cartracker.model.Trip;
import dwe.holding.generic.cartracker.repository.DriveRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DriveServiceImpl implements DriveService {

    private final DriveRepository driveRepository;

    DriveServiceImpl(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    public UUID saveRecord(Trip car) {
        return driveRepository.save(car).getId();
    }

    @Override
    public List<String> getAllAsCsv() {
        Iterable<Trip> carEntities = driveRepository.findAllOrderByDriveDateAsc();
        return StreamSupport.stream(carEntities.spliterator(), false)
                .map(Trip::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<Trip> getAllAsList(String name) {
        return driveRepository.findByPersonOrderByDriveDateAsc(name);
    }


    @Override
    public void deleteCarRecords() {
        driveRepository.deleteAll();
    }

    @Override
    public String getHtmlStringOf(UUID id) {
        Optional<Trip> car = driveRepository.findById(id);
        return (car.isPresent()) ? car.get().toHtmlString() : "";
    }

    @Override
    public List<Trip> getAllAsList() {
        return new ArrayList<>(driveRepository.findAllOrderByDriveDateAsc());
    }

    @Override
    public Integer getLatestTotal(String carType) {
        return driveRepository.findFirstByCarTypeOrderByKmTotalDesc(carType).getKmTotal();
    }
}