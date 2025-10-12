package dwe.holding.generic.cartracker.service;

import dwe.holding.generic.cartracker.model.Trip;

import java.util.List;
import java.util.UUID;


public interface DriveService {

    UUID saveRecord(Trip car);

    List<String> getAllAsCsv();

    void deleteCarRecords();

    String getHtmlStringOf(UUID id);

    List<Trip> getAllAsList(String name);
    List<Trip> getAllAsList();

    Integer getLatestTotal(String name);
}