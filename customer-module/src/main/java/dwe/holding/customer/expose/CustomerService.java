package dwe.holding.customer.expose;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.mapper.CustomerMapper;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;
    private final PetRepository petRepository;

    public Customer searchCustomer(Long customerId) {
        return customerMapper.toCustomer(
                customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow()
        );
    }

    public Customer searchCustomerAndPet(Long customerId, Long petId) {
        Customer customer = customerMapper.toCustomer(customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow());
        customer.pets.stream().filter(pet -> pet.id.equals(petId)).findFirst().orElseThrow();
        return customer;
    }

    public Customer searchCustomerFromPet(Long petId) {
        return customerMapper.toCustomer(customerRepository.findByPets_IdAndMemberId(petId, AutorisationUtils.getCurrentUserMid()));
    }

    public dwe.holding.customer.client.model.Pet getPet(Long customerId, Long petId) {
        dwe.holding.customer.client.model.Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        Optional<dwe.holding.customer.client.model.Pet> pet = customer.getPets().stream().filter(custPet -> custPet.getId().equals(petId)).findFirst();
        return pet.orElseThrow();
    }

    public void updatePetDeceased(Long id) {
        dwe.holding.customer.client.model.Pet pet = petRepository.findByIdAndMemberId(id, AutorisationUtils.getCurrentUserMid());
        pet.setDeceased(YesNoEnum.Yes);
        pet.setDeceasedDate(LocalDate.now());
        petRepository.save(pet);
    }

    @Transactional
    public void updateCustomerBalance(Long customerId, BigDecimal balance) {
        dwe.holding.customer.client.model.Customer customer = customerRepository.findById(customerId).orElseThrow();
        customer.setBalance(balance);
        customerRepository.save(customer);
    }

    public record Customer(
            Long id,
            String customerSalutation,
            String customerName,
            String email,
            String homePhone,
            String workPhone,
            String mobilePhone,
            String address2, // TODO rename to streetAndNumberAndExtention
            String city,
            String zipCode,
            Long memberId,
            CustomerStatusEnum status,
            List<Pet> pets,
            BigDecimal balance
    ) {
        public String formattedHtmlAddress() {
            return dwe.holding.customer.client.model.Customer.formattedHtmlAddress(address2, zipCode, city);
        }
    }

    public record Pet(
            Long id,
            String name,
            LocalDate birthday,
            boolean deceased,
            String species,
            SexTypeEnum sex,
            String idealWeight,
            YesNoEnum allergies,
            String allergiesDescription,
            YesNoEnum gpwarning,
            String gpwarningDescription,
            YesNoEnum insured,
            String insuredBy,
            LocalDate chipDate,
            String chipTattooId) {
        public boolean hasAllergies() {
            return allergies.booleanValue();
        }

        public boolean isDangerous() {
            return gpwarning.booleanValue();
        }

        public boolean isInsured() {
            return insured.booleanValue();
        }

        public boolean hasWarning() {
            return gpwarning.booleanValue() || allergies.booleanValue();
        }

        public String getWarningShort() {
            return "<span>" +
                    (insured.equals(YesNoEnum.Yes)  ? "I " : "<span class='red-text'>I </span>") +
                    (gpwarning.equals(YesNoEnum.No) ? "D " : "<span class='red-text'>D </span>") +
                    (allergies.equals(YesNoEnum.No) ? "A " : "<span class='red-text'>A </span>") +
                    "</span>";
        }

        // duplicate in Pet
        public String getWarningInfo() {
            StringBuilder warning = new StringBuilder();
            if (insured.equals(YesNoEnum.Yes)) {
                warning.append("I: ").append(insuredBy).append('\n');
            } else {
                warning.append("I: ").append('\n');
            }
            if (gpwarning.equals(YesNoEnum.Yes)) {
                warning.append("D: ").append(gpwarningDescription).append('\n');
            } else {
                warning.append("D: ").append('\n');
            }
            if (allergies.equals(YesNoEnum.Yes)) {
                warning.append("A: ").append(allergiesDescription).append('\n');
            } else {
                warning.append("A: ").append('\n');
            }
            return warning.toString();
        }
    }
}