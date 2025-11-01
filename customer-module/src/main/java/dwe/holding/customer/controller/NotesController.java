package dwe.holding.customer.controller;

import dwe.holding.customer.model.Customer;
import dwe.holding.customer.model.Notes;
import dwe.holding.customer.model.Pet;
import dwe.holding.customer.repository.CustomerRepository;
import dwe.holding.customer.repository.NotesRepository;
import dwe.holding.customer.repository.PetRepository;
import dwe.holding.generic.admin.expose.UserService;
import dwe.holding.generic.shared.model.frontend.PresentationElement;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import dwe.holding.customer.lookup.repository.LookupNotePurposeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
public class NotesController {
    private final NotesRepository notesRepository;
    private final UserService userService;
    private final ValidateCustomer validateCustomer;
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final LookupNotePurposeRepository lookupNotePurposeRepository;

    public NotesController(NotesRepository notesRepository, UserService userService, ValidateCustomer validateCustomer,
                           PetRepository petRepository, CustomerRepository customerRepository, LookupNotePurposeRepository lookupNotePurposeRepository) {
        this.notesRepository = notesRepository;
        this.userService = userService;
        this.validateCustomer = validateCustomer;
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
        this.lookupNotePurposeRepository = lookupNotePurposeRepository;
    }

    @GetMapping("/customer/{customerId}/notes")
    String list(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        model.addAttribute("notes", notesRepository.getByCustomerId(customerId));
        setModel(model, customerId);
        return "customer-module/notes/list";
    }

    @GetMapping("/customer/{customerId}/note")
    String newRecord(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        Customer customer = customerRepository.findById(customerId).orElseThrow();
        model.addAttribute("note", new Notes());
        setModelOneRecord(model, customer);

        setModel(model, customer.getId());
        return "customer-module/notes/action";
    }


    @GetMapping("/customer/{customerId}/notes/{notesId}")
    @Transactional
    String editRecord(@PathVariable Long customerId, @PathVariable Long notesId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        Notes note = notesRepository.findById(notesId).orElseThrow();
        model.addAttribute("note", note.getPet().getCustomer().getId().equals(customerId) ? note : new Notes());
        setModelOneRecord(model, note.getPet().getCustomer());
        setModel(model, note.getPet().getCustomer().getId());
        return "customer-module/notes/action";
    }

    @PostMapping("/customer/{customerId}/note")
    @Transactional
    String saveRecord(@PathVariable Long customerId, Notes formNote, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";

        Pet pet = petRepository.findById(formNote.getPet().getId()).orElseThrow();
        if (!pet.getCustomer().getId().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/customer/" + customerId + "/reminders";
        }

        if (formNote.isNew()) {
            notesRepository.save(
                    Notes.builder().note(formNote.getNote())
                            .noteDate(formNote.getNoteDate())
                            .pet(pet)
                            .purpose(formNote.getPurpose())
                            .staffMember(formNote.getStaffMember())
                            .build()
            );
        } else {
            notesRepository.save(formNote);
        }
        return "redirect:/customer/customer/" + customerId + "/notes";
    }


    void setModel(Model model, Long customerId) {
        model.addAttribute("activeMenu", "notes");
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("customerId", customerId);
    }

    record DoubleText(String id, String name) {
    }

    private void setModelOneRecord(Model model, Customer customer) {
        model.addAttribute("petsList", customer.getPets().stream().map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased(), true)).toList());
        model.addAttribute("staffList", userService.getStaffMembers(77L).stream().map(rec -> new DoubleText(rec.getName(), rec.getName())).toList()); // TODO: AutorisationUtils.getCurrentUserMid());
        model.addAttribute("purposeList", lookupNotePurposeRepository.getByMemberId(77L).stream().map(rec -> new DoubleText(rec.getPreDefinedPurpose(), rec.getPreDefinedPurpose())).toList());
    }
}