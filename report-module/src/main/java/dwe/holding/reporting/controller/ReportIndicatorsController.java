package dwe.holding.reporting.controller;

import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.reporting.model.MeasureIndicator;
import dwe.holding.salesconsult.consult.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller
public class ReportIndicatorsController {

    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;

    public MeasureIndicator queryMeasurementIndicators(LocalDate from, LocalDate till, Long memberId) {
        MeasureIndicator indicator = new MeasureIndicator();

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime tillDateTime = till.atTime(23, 59, 59);

        // Customer per year
        List<Object[]> customersPerYear = customerRepository.countCustomersPerYear(memberId);
        List customerList = customersPerYear.stream()
                .map(row -> new YearCount(row[0].toString(), row[1].toString()))
                .toList();
        indicator.setCustomerPerYear(customerList);

        // Number of visits
        Long visitCount = appointmentRepository.countVisitsByDateRange(
                fromDateTime, tillDateTime, memberId
        );
        indicator.setNumberOfVisits(visitCount);

        // Number of appointments
        Long appointmentCount = appointmentRepository.countAppointmentsByDateRange(
                fromDateTime, tillDateTime, memberId
        );
        indicator.setNumberOfAppointments(appointmentCount);

        if (appointmentCount > 0) {
            // New customers
            Long newCustomerCount = customerRepository.countNewCustomers(
                    fromDateTime, tillDateTime, fromDateTime, tillDateTime, memberId
            );
            indicator.setNewCustomers(newCustomerCount);

            // Active customers (last 12 months from till date)
            LocalDateTime fromActiveCustomers = tillDateTime.minusYears(1);
            Long activeCustomerCount = customerRepository.countActiveCustomers(
                    fromActiveCustomers, tillDateTime, memberId
            );
            indicator.setNumberOfActiveCustomers(activeCustomerCount);

            // Potential patients for vaccination
            List<Object[]> patientsBySpecies = petRepository.countPatientsBySpeciesForActiveCustomers(
                    fromActiveCustomers, tillDateTime, memberId
            );
            indicator.setPotentialPatientsVaccination(new ArrayList<>(patientsBySpecies));
        }
        return indicator;
    }

    record YearCount(String year, String count) {
    }
}
