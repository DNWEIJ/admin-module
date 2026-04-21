package dwe.holding.customer.client.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.admin.util.ControllerHelper;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.type.SexTypeEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.LookupBreedsRepository;
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
@RequestMapping("/customer")
@Slf4j
@AllArgsConstructor
public class PetController {
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final LookupSpeciesRepository lookupSpeciesRepository;
    private final LookupBreedsRepository lookupBreedsRepository;


    @GetMapping("/customer/{customerId}/pets")
    String list(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("pets", petRepository.findByCustomer_IdOrderByDeceasedAsc(customer.getId()));
        setModel(model, customerId, null);

        return "customer-module/pet/list";
    }

    @GetMapping("/customer/{customerId}/pet/{petId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long petId, Model model, HttpServletRequest request) {
        Pet pet = petRepository.findById(petId).orElseThrow();
        model.addAttribute("pet", pet.getCustomer().getId().equals(customerId) ? pet : new Pet());
        setModel(model, pet.getCustomer().getId(), pet);
        final boolean isHtmx = ControllerHelper.getHtmxAndAddToModel(request, model);
        return isHtmx ? "customer-module/pet/petformmodal" : "customer-module/pet/action";
    }


    @GetMapping("/customer/{customerId}/pet")
    String setupForNewRecord(@PathVariable Long customerId, Model model, HttpServletRequest request) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        final boolean isHtmx = ControllerHelper.getHtmxAndAddToModel(request, model);

        // on error, we have a form
        if (model.containsAttribute("petForm")) {
            setModel(model, customer.getId(), (Pet) model.getAttribute("petForm"));
            return isHtmx ? "admin-module/modal/error" : "customer-module/pet/action";
        } else {
            setModel(model, customer.getId(), Pet.builder()
                    .allergies(YesNoEnum.No).deceased(YesNoEnum.No).gpwarning(YesNoEnum.No).insured(YesNoEnum.No)
                    .build()
            );
        }
        return isHtmx ? "customer-module/pet/petformmodal" : "customer-module/pet/action";
    }


    @GetMapping("/customer/pet/species/{species}")
    String getBreed(@PathVariable String species, Model model) {

        model.addAttribute("flatData", lookupBreedsRepository.findBySpeciesName(species).stream()
                .map(f -> "<option value='" + f.getBreed() + "'>" + f.getBreed() + "</option>")
                .collect(Collectors.joining()));
        return "fragments/elements/flatData";
    }


    @PostMapping("/customer/{customerId}/pet/{petId}")
    String savePet(@PathVariable Long customerId, @PathVariable Long petId, @Valid Pet petForm,
                   RedirectAttributes redirect, HttpServletRequest request, HttpServletResponse response, Model model) {

        customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        Pet pet = petRepository.findById(petForm.getId()).orElseThrow();

        if (!pet.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/" + customerId + "/pets";
        }
        redirect.addFlashAttribute("message", "label.saved");

        petRepository.save(copyPetFromForm(petForm,pet));

        final boolean isHtmx = ControllerHelper.getHtmxAndAddToModel(request, model);
        if (isHtmx) {
            response.setHeader("HX-Trigger", "closeModal");
        }
        return isHtmx ? "fragments/elements/empty" : "redirect:/customer/customer/" + customerId + "/pets";
    }

    @PostMapping("/customer/{customerId}/pet")
    String saveNewPet(@PathVariable Long customerId, @Valid Pet petForm, RedirectAttributes redirect, HttpServletRequest request, HttpServletResponse response, Model model) {
        final boolean isHtmx = ControllerHelper.getHtmxAndAddToModel(request, model);
        // not sure why we do this.... we are also called for udpate instead of only for new
        if (petForm.getId() != null) {
            return savePet(customerId, petForm.getId(), petForm, redirect, request, response, model);
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
            if (isHtmx) {
                model.addAttribute("message", "pet.error.name");
                model.addAttribute("messageClass", "alert-error");
                return "admin-module/modal/message::notification";
            }
            redirect.addFlashAttribute("message", "pet.error.name");
            redirect.addFlashAttribute("messageClass", "alert-error");
            redirect.addFlashAttribute("petForm", petForm);
            return "redirect:/customer/customer/" + customerId + "/pet";
        }

        Pet savedPet = petRepository.save(copyPetFromForm(petForm, Pet.builder().customer(customer).build()));
        customer.getPets().add(savedPet);
        customerRepository.save(customer);
        redirect.addFlashAttribute("message", "label.saved");
        if (isHtmx) {
            response.setHeader("HX-Trigger", "closeModal");
        }
        return isHtmx ? "fragments/elements/empty" : "redirect:/customer/customer/" + customerId + "/pets";
    }

    private Pet copyPetFromForm(Pet petForm, Pet pet) {
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
        return pet;
    }


    void setModel(Model model, Long customerId, Pet pet) {
        // TODO remove all, no -1 anymore
        List<Long> listIds = List.of(AutorisationUtils.getCurrentUserMid(), -1L);

        model.addAttribute("activeMenu", "pets")
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("sexTypeList", SexTypeEnum.getWebList())
                .addAttribute("customerId", customerId);
        if (pet != null) {
            model.addAttribute("pet", pet)
                    .addAttribute("speciesList", lookupSpeciesRepository.findByMemberId(AutorisationUtils.getCurrentUserMid())
                            .stream().map(
                                    f -> new PresentationElement(f.getSpecies(), f.getSpecies())
                            )
                            .sorted(comparing(PresentationElement::getName)).toList()
                    )
                    .addAttribute("breedList", (pet.getSpecies() == null || pet.getSpecies().isBlank()) ? List.of() :
                            lookupBreedsRepository.findBySpeciesName(pet.getSpecies()).stream()
                                    .map(breed -> new PresentationElement(breed.getBreed(), breed.getBreed()))
                                    .sorted(comparing(PresentationElement::getName)).toList()
                    );
        }
    }
}