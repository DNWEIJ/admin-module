package dwe.holding.customer.client.controller;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.LookupBreedsRepository;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
public class PetController {
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final LookupSpeciesRepository lookupSpeciesRepository;
    private final LookupBreedsRepository lookupBreedsRepository;
    private final ValidateCustomer validateCustomer;

    public PetController(PetRepository petRepository, CustomerRepository customerRepository, LookupSpeciesRepository lookupSpeciesRepository,
                         LookupBreedsRepository lookupBreedsRepository, ValidateCustomer validateCustomer) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
        this.lookupSpeciesRepository = lookupSpeciesRepository;
        this.lookupBreedsRepository = lookupBreedsRepository;
        this.validateCustomer = validateCustomer;
    }


    @GetMapping("/customer/{customerId}/pets")
    String list(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {

        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        model.addAttribute("pets", petRepository.findByCustomer_IdOrderByDeceasedAsc(customerId));
        setModel(model, customerId, null);
        return "customer-module/pet/list";
    }

    @GetMapping("/customer/{customerId}/pet")
    String newRecord(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        setModel(model, customerId, Pet.builder()
                .allergies(YesNoEnum.No).deceased(YesNoEnum.No).gpwarning(YesNoEnum.No).insured(YesNoEnum.No)
                .build()
        );
        return "customer-module/pet/action";
    }

    @PostMapping("/customer/{customerId}/pet")
    String savePet(@PathVariable Long customerId, @Valid Pet petForm, RedirectAttributes redirect) {

        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        if (petForm.isNew()) {
            Customer customer = customerRepository.findById(customerId).orElseThrow();
            Pet savedPet = petRepository.save(
                    Pet.builder()
                            .name(petForm.getName()).birthday(petForm.getBirthday()).idealWeight(petForm.getIdealWeight())
                            .species(petForm.getSpecies()).breed(petForm.getBreed()).breedOther(petForm.getBreedOther()).sex(petForm.getSex())
                            .briefDescription(petForm.getBriefDescription())
                            .allergies(petForm.getAllergies()).insured(petForm.getInsured()).deceased(petForm.getDeceased()).gpwarning(petForm.getGpwarning())
                            .allergiesDescription(petForm.getAllergiesDescription()).insuredBy(petForm.getInsuredBy())
                            .deceasedDate(petForm.getDeceasedDate()).gpwarningDescription(petForm.getGpwarningDescription())

                            .passportNumber(petForm.getPassportNumber()).chipTattooId(petForm.getChipTattooId()).chipDate(petForm.getChipDate())
                            .comments(petForm.getComments())

                            .customer(customer)
                            .build()
            );
            customer.getPets().add(savedPet);
            customerRepository.save(customer);
            return "redirect:/customer/customer/" + customer.getId() + "/pets";
        } else {
            Pet pet = petRepository.findById(petForm.getId()).orElseThrow();
            if (!pet.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
                return "redirect:/customer/" + customerId + "/pets";
            }
            petRepository.save(pet);
            return "redirect:/customer/customer/" + customerId + "/pets";
        }
    }

    @GetMapping("/customer/{customerId}/pet/{petId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long petId, Model model, RedirectAttributes redirect) {

        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        Pet pet = petRepository.findById(petId).orElseThrow();
        model.addAttribute("pet", pet.getCustomer().getId().equals(customerId) ? pet : new Pet());
        setModel(model, pet.getCustomer().getId(), pet);
        return "customer-module/pet/action";
    }


    void setModel(Model model, Long customerId, Pet pet) {
        model.addAttribute("activeMenu", "pets");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("sexTypeList", SexTypeEnum.getWebList());
        model.addAttribute("customerId", customerId);
        model.addAttribute("pet", pet);
        model.addAttribute("speciesList", lookupSpeciesRepository.getList(AutorisationUtils.getCurrentUserMid()) // TODO replace by AutorisationUtils.getCurrentUserMid()
                .stream().map(
                        f -> new PresentationElement(f.getId(), f.getSpecies(), true)
                )
                .sorted(Comparator.comparing(PresentationElement::getName)).toList()
        );
        model.addAttribute("breedList", lookupBreedsRepository.getList(AutorisationUtils.getCurrentUserMid()) // TODO replace by AutorisationUtils.getCurrentUserMid()
                .stream().map(
                        f -> new PresentationElement(f.getId(), f.getBreed(), true)
                )
                .sorted(Comparator.comparing(PresentationElement::getName)).toList()

        );
    }
}