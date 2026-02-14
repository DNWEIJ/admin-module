package dwe.holding.customer.client.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.lookup.LookupBreeds;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.LookupSpeciesRepository;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
@AllArgsConstructor
public class PetController {
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final LookupSpeciesRepository lookupSpeciesRepository;

    @GetMapping("/customer/{customerId}/pets")
    String list(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("pets", petRepository.findByCustomer_IdOrderByDeceasedAsc(customer.getId()));
        setModel(model, customerId, null);

        return "customer-module/pet/list";
    }

    @GetMapping("/customer/{customerId}/pet")
    String setupForNewRecord(@PathVariable Long customerId, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        // on error, we have a form
        if (model.containsAttribute("petForm")) {
            setModel(model, customer.getId(), (Pet) model.getAttribute("petForm"));
            return getHtmxAndAddToModel(request, model) ? "admin-module/modal/error" : "customer-module/pet/action";
        } else {
            setModel(model, customer.getId(), Pet.builder()
                    .allergies(YesNoEnum.No).deceased(YesNoEnum.No).gpwarning(YesNoEnum.No).insured(YesNoEnum.No)
                    .build()
            );
        }
        return getHtmxAndAddToModel(request, model) ? "customer-module/pet/petformmodal" : "customer-module/pet/action";
    }

    @PostMapping("/customer/{customerId}/pet/{petId}")
    String savePet(@PathVariable Long customerId, @PathVariable Long petId, @Valid Pet petForm, RedirectAttributes redirect, HttpServletRequest request, Model model) {
        Pet pet = petRepository.findById(petForm.getId()).orElseThrow();
        if (!pet.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/" + customerId + "/pets";
        }
        pet.setVersion(petForm.getVersion());
        pet.setName(petForm.getName());
        pet.setBirthday(petForm.getBirthday());
        pet.setIdealWeight(petForm.getIdealWeight());
        pet.setSpecies(petForm.getSpecies());
        pet.setBreed(petForm.getBreed());
        pet.setSex(petForm.getSex());
        pet.setBriefDescription(petForm.getBriefDescription());
        pet.setAllergies(petForm.getAllergies());
        pet.setInsured(petForm.getInsured());
        pet.setDeceased(petForm.getDeceased());
        pet.setGpwarning(petForm.getGpwarning());
        pet.setAllergiesDescription(petForm.getAllergiesDescription());
        pet.setInsuredBy(petForm.getInsuredBy());
        pet.setDeceasedDate(petForm.getDeceasedDate());
        pet.setGpwarningDescription(petForm.getGpwarningDescription());
        pet.setPassportNumber(petForm.getPassportNumber());
        pet.setChipTattooId(petForm.getChipTattooId());
        pet.setChipDate(petForm.getChipDate());
        pet.setComments(petForm.getComments());

        petRepository.save(pet);
        return getHtmxAndAddToModel(request, model) ? "customer-module/pet/petformmodal" : "redirect:/customer/customer/" + customerId + "/pets";
    }

    @PostMapping("/customer/pet/breed/{id}")
    String getBreed(@PathVariable Long id, Model model){
        model.addAttribute("flatData",
                model.addAttribute("speciesList", lookupSpeciesRepository.findById(id).orElseThrow().getBreeds()
                        .stream().sorted(comparing(LookupBreeds::getBreed)).map(
                                f -> "<option='" + f.getId() + "'>" + f.getBreed() + "</option>"
                        ).collect(Collectors.joining()))
                );
        return "fragments/elements/flatData";
    }

    @PostMapping("/customer/{customerId}/pet")
    String saveNewPet(@PathVariable Long customerId, @Valid Pet petForm, RedirectAttributes redirect, HttpServletRequest request, HttpServletResponse response, Model model) {
        if (petForm.getId() != null) {
            return savePet(customerId, petForm.getId(), petForm, redirect, request, model);
        }
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        if (customer.getPets().stream()
                .map(o -> o.getName().replaceAll("\\s+", "").toLowerCase())
                .anyMatch(n -> n.equals(
                                petForm.getName().replaceAll("\\s+", "").toLowerCase()
                        )
                )
        ) {
            // pet already exisits; error case
            boolean isHtmx = getHtmxAndAddToModel(request, model);
            if (isHtmx) {
                model.addAttribute("message", "pet.error.name");
                model.addAttribute("messageClass", "alert-error");
                setModel(model, customer.getId(), petForm);
                return "customer-module/pet/petformmodal";
            }
            redirect.addFlashAttribute("message", "pet.error.name");
            redirect.addFlashAttribute("messageClass", "alert-error");
            redirect.addFlashAttribute("petForm", petForm);

            return "redirect:/customer/customer/" + customerId + "/pet";
        }

        Pet savedPet = petRepository.save(
                Pet.builder()
                        .name(petForm.getName()).birthday(petForm.getBirthday()).idealWeight(petForm.getIdealWeight())
                        .species(petForm.getSpecies()).breed(petForm.getBreed()).sex(petForm.getSex())
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
        redirect.addFlashAttribute("message", "label.saved");
        boolean isHtmx = getHtmxAndAddToModel(request, model);
        if (isHtmx) {
            response.setHeader("HX-Trigger", "closeModal");
        }
        return isHtmx ? "fragments/elements/empty" : "redirect:/customer/customer/" + customerId + "/pets";
    }

    @GetMapping("/customer/{customerId}/pet/{petId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long petId, Model model, HttpServletRequest request) {
        Pet pet = petRepository.findById(petId).orElseThrow();
        model.addAttribute("pet", pet.getCustomer().getId().equals(customerId) ? pet : new Pet());
        setModel(model, pet.getCustomer().getId(), pet);
        boolean isHtmx = getHtmxAndAddToModel(request, model);
        return isHtmx ? "customer-module/pet/petformmodal" : "customer-module/pet/action";
    }

    void setModel(Model model, Long customerId, Pet pet) {
        List<Long> listIds = List.of(AutorisationUtils.getCurrentUserMid(), -1L);
        model.addAttribute("activeMenu", "pets");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("sexTypeList", SexTypeEnum.getWebList());
        model.addAttribute("customerId", customerId);
        model.addAttribute("pet", pet);
        model.addAttribute("speciesList", lookupSpeciesRepository.findByMemberIdIn(listIds)
                .stream().map(
                        f -> new PresentationElement(f.getId(), f.getSpecies())
                )
                .sorted(comparing(PresentationElement::getName)).toList()
        );
    }

    private static boolean getHtmxAndAddToModel(HttpServletRequest request, Model model) {
        final boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        model.addAttribute("isHtmx", isHtmx);
        return isHtmx;
    }
}