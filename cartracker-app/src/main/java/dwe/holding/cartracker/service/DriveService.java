package dwe.holding.cartracker.service;

import dwe.holding.cartracker.model.Trip;

import java.util.List;
  


public interface DriveService {

      Long saveRecord(Trip car);

    List<String> getAllAsCsv();

    void deleteCarRecords();

    String getHtmlStringOf(  Long id);

    List<Trip> getAllAsList(String name);
    List<Trip> getAllAsList();

    Integer getLatestTotal(String name);
}