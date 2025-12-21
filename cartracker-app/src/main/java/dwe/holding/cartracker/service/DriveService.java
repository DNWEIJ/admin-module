package dwe.holding.cartracker.service;

import dwe.holding.cartracker.model.Trip;

import java.util.List;


public interface DriveService {

    Long saveRecordForPaid(Trip trip);

    Long saveRecordForPaid(Long tripId);

    Trip getTripById(Long id);

    List<String> getAllAsCsv();

    void deleteCarRecords();

    String getHtmlStringOf(Long id);

    List<Trip> getAllAsList(String name);

    List<Trip> getAllAsList();

    Integer getLatestTotal(String name);
}