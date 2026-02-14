package dwe.holding.cartracker.service;


import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.cartracker.model.Trip;
import dwe.holding.cartracker.repository.DriveRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DriveServiceImpl implements DriveService {

    private final DriveRepository driveRepository;

    DriveServiceImpl(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    public   Long saveRecordForPaid(Trip car) {
        car.setMemberId(AutorisationUtils.getCurrentUserMid());
        car.setLocalMemberId(AutorisationUtils.getCurrentUserMid());
        return driveRepository.save(car).getId();
    }

    public Long saveRecordForPaid(Long tripId) {
        Trip trip = driveRepository.findByIdAndMemberId(tripId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        trip.setPaid(true);
        return driveRepository.save(trip).getId();
    }

    @Override
    public Trip getTripById(Long id) {
        return driveRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid()).orElseThrow();
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
    public String getHtmlStringOf(Long id) {
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