package dwe.holding.salesconsult.consult.service;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.EstimateForPet;
import dwe.holding.salesconsult.consult.model.Estimatelineitem;
import dwe.holding.salesconsult.consult.repository.EstimateForPetRepository;
import dwe.holding.salesconsult.consult.repository.EstimateLineitemRepository;
import dwe.holding.salesconsult.consult.repository.EstimateRepository;
import dwe.holding.salesconsult.sales.model.CostCalc;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EstimateService {
    private final EstimateForPetRepository estimateForPetRepository;
    private final EstimateRepository estimateRepository;
    private final EstimateLineitemRepository estimateLineitemRepository;
    private final CustomerService customerService;

    public List<EstimateForm> listEstimatesForCustomerPets(List<Long> petIds) {
        return estimateForPetRepository.findByMemberIdAndPet_IdInOrderByEstimate_EstimateDateDesc(AutorisationUtils.getCurrentUserMid(), petIds)
                .stream().map(efp ->
                        new EstimateForm(
                                efp.getEstimate().getId(),
                                efp.getPet().getId(),
                                efp.getEstimate().getEstimateDate(),
                                efp.getPurpose(),
                                efp.getEstimate().getEstimatelineitems().stream().map(CostCalc::getTotalIncTax).reduce(java.math.BigDecimal::add).orElse(BigDecimal.ZERO),
                                efp.getEstimate().getTransToVisit(),
                                Objects.isNull(efp.getEstimate().getTransToVisit())
                        )
                ).toList();
    }

    public record EstimateForm(Long id, Long petId, LocalDate estimateDate, String purpose, BigDecimal totalAmount, LocalDate transToVisit, boolean canEdit) {
    }

    public Estimate createEstimate(List<AppointmentVisitService.CreatePet> pets, Long customerId) {

        Estimate estimate = Estimate.builder().estimateDate(LocalDate.now()).build();

        estimate.setEstimateForPets(
                pets.stream().map(formPet ->
                        EstimateForPet.builder()
                                .pet(customerService.getPet(customerId, formPet.id()))
                                .estimate(estimate)
                                .purpose(formPet.purpose())
                                .comments("")
                                .build()
                ).collect(Collectors.toSet()));
        estimateRepository.save(estimate);

        return estimate;
    }

    public Estimate getEstimate(Long estimateId, Long petId) {
        Estimate estimate = estimateRepository.findByIdAndMemberId(estimateId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        // validate pet
        estimate.getEstimateForPets().stream().filter(estimateForPet -> estimateForPet.getPet().getId().equals(petId)).findFirst().orElseThrow();
        return estimate;
    }

    public List<Estimatelineitem> saveEstimateLineItems(Collection<Estimatelineitem> estimateList) {
        return estimateLineitemRepository.saveAll(estimateList);
    }

    public EstimateForPet saveEstimateForPet(Long estimateForPetId, String purpose, String comments) {
        EstimateForPet estimateForPet = estimateForPetRepository.findByIdAndMemberId(estimateForPetId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        estimateForPet.setPurpose(purpose);
        estimateForPet.setComments(comments);
        estimateForPet.setMemberId(AutorisationUtils.getCurrentUserMid());
        return estimateForPetRepository.save(estimateForPet);
    }

    public List<Estimatelineitem> getAllLineItems(Long estimateId, Long petId) {
        return estimateLineitemRepository.findByEstimate_IdAndPet_Id(estimateId, petId);
    }
}