package dwe.holding.generic.cartracker.service;

import dwe.holding.generic.cartracker.model.Car;
import dwe.holding.generic.cartracker.model.Trip;

import java.util.List;
import java.util.Map;


public interface CarService {

    List<String> getAllAsCsv();

    List<Car> getAllAsList();

    List<String> getAllNames();

    Long getLatestTotal(String name);

    Map<String, Integer> getAllNameAndTotalKm();

    void saveRecord(Trip drive);
}