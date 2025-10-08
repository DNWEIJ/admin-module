package dwe.holding.generic.cartracker.service;

import dwe.holding.generic.cartracker.model.CarEntity;
import dwe.holding.generic.cartracker.model.TripEntity;

import java.util.List;
import java.util.Map;


public interface CarService {

    List<String> getAllAsCsv();

    List<CarEntity> getAllAsList();

    List<String> getAllNames();

    Long getLatestTotal(String name);

    Map<String, Integer> getAllNameAndTotalKm();

    void saveRecord(TripEntity drive);
}