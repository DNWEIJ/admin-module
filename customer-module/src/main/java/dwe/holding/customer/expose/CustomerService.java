package dwe.holding.customer.expose;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.mapper.CustomerMapper;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public Customer searchCustomer(Long customerId) {

        return customerMapper.toCustomer(
                customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow()
        );
    }

    public Customer searchCustomerFromPet(Long patientId) {
        return customerMapper.toCustomer(
                customerRepository.findByPets_IdAndMemberId(patientId, AutorisationUtils.getCurrentUserMid())
        );
    }

    public dwe.holding.customer.client.model.Pet getPet(Long customerId, Long petId) {
        dwe.holding.customer.client.model.Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        Optional<dwe.holding.customer.client.model.Pet> pet = customer.getPets().stream().filter(custPet -> custPet.getId().equals(petId)).findFirst();
        return pet.orElseThrow();
    }

    public record Customer(
            Long id,
            String customerName,
            String email,
            String homePhone,
            String workPhone,
            String mobilePhone,
            String address1,
            String address2,
            String address3,
            Long memberId,
            CustomerStatusEnum status,
            List<Pet> pets
    ) {
    }

    public record Pet(
            Long id,
            String name,
            LocalDate birthday,
            String age,
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
    }
}