package dwe.holding.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasureIndicator {
    Long numberOfActiveCustomers;
    Long newCustomers;
    List potentialPatientsVaccination;
    Long numberOfAppointments;
    Long numberOfVisits;
    Double turnOverEx;
    Double turnOverInc;
    List customerPerYear;
}
