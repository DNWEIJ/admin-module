package dwe.holding.vmas.controller;

import dwe.holding.VmasApplication;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.repository.CustomerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@SpringBootTest(classes = VmasApplication.class, properties = "spring.profiles.active=vmas")
class IndexControllerTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void doTest() throws FileNotFoundException {
        // change address
        File file = new File("customer_addresses.txt");
        List<Customer> customers = customerRepository.findAll();
        try (PrintWriter writer = new PrintWriter(file)) {
            customers.stream()
                    .filter(c -> c.getAddress2() != null && !c.getAddress2().isBlank())
                    .map(c -> new Object() {
                        final Customer customer = c;
                        final AddressParts parts = splitAddress(c.getAddress2());
                    }).forEach(x -> writer.println(
                                    String.format(
                                            "%-8s | %-50s | %-30s | %-20s",
                                            x.customer.getId(),
                                            x.customer.getAddress2(),
                                            x.parts.street,
                                            x.parts.housePart != null ? x.parts.housePart : ""
                                    )
                            )
                    );
        }
    }

    @Test
    /**
     * Ensure to turn off auditaware for the tenant id, it is not needed for this test
     */
    void updateRecords() throws FileNotFoundException {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        List<Customer> customers = customerRepository.findAll();
        List<Customer> updatedCustomers = customers.stream()
                .filter(c -> c.getAddress2() != null && !c.getAddress2().isBlank())
                .map(customer -> {
                    // valid to the regex as much as possible; if not log the info
                    customer.setZipCode(changeZipCode(customer));

                    Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
                    if (!violations.isEmpty()) {
                        System.out.println("Customer ID " + customer.getId() + " has invalid ZIP: " + customer.getZipCode());
                    }
                    AddressParts parts = splitAddress(customer.getAddress2());
                    // Update the record fields
                    customer.setStreet(parts.street());
                    customer.setStreetNumber(parts.housePart());
                    return customer;
                })
                .toList();
        customerRepository.saveAll(updatedCustomers);
    }

    // upper case and remove spaces
    private String changeZipCode(Customer customer) {
        String zipCode = customer.getZipCode();
        zipCode = zipCode == null ? null : zipCode.trim().replaceAll(" ", "").toUpperCase();
        // zipcode is null or empty
        if (zipCode == null || zipCode.isBlank()) {
            return null;
        }
        return zipCode;
    }

    public record AddressParts(String street, String housePart) {
    }

    public static AddressParts splitAddress(String address) {
        if (address == null || address.isBlank()) {
            return new AddressParts("", "");
        }

        String[] elements = address.trim().split("\\s+");
        if (elements.length == 1 && elements[0].chars().allMatch(Character::isDigit)) {
            return new AddressParts("", elements[0]);
        }

        for (int i = 1; i < elements.length; i++) {
            // If the element starts with a number, split here
            if (elements[i].matches("^\\d.*")) {
                String street = String.join(" ", Arrays.copyOfRange(elements, 0, i));
                String housePart = String.join(" ", Arrays.copyOfRange(elements, i, elements.length));
                return new AddressParts(street, housePart);
            }
        }

        // No number found after first element → all is street, housePart empty
        return new AddressParts(String.join(" ", elements), "");
    }
}