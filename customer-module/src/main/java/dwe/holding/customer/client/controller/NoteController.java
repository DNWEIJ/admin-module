package dwe.holding.customer.client.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Note;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.repository.NoteRepository;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.customer.lookup.repository.NotePurposeLookupRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
@AllArgsConstructor
public class NoteController {
    private final NoteRepository noteRepository;
    private final UserService userService;
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final NotePurposeLookupRepository notePurposeLookupRepository;

    @GetMapping("/customer/{customerId}/notes")
    String list(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("notes", noteRepository.findByPet_Customer_IdOrderByNoteDateDesc(customer.getId()));
        setModel(model, customerId);
        return "customer-module/notes/list";
    }

    @GetMapping("/customer/{customerId}/note")
    public String newNote(@PathVariable Long customerId, Model model) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        model.addAttribute("note",
                Note.builder().
                        noteDate(LocalDate.now())
                        .staffMember(AutorisationUtils.getCurrentUserName())
                        .pet(customer.getPets().iterator().next()).build());

        setModelOneRecord(model, customer);

        setModel(model, customer.getId());
        return "customer-module/notes/action";
    }


    @GetMapping("/customer/{customerId}/notes/{notesId}")
    String editRecord(@PathVariable Long customerId, @PathVariable Long notesId, Model model, RedirectAttributes redirect) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        Note note = noteRepository.findById(notesId).orElseThrow();
        model.addAttribute("note", note.getPet().getCustomer().getId().equals(customer.getId()) ? note : new Note());
        setModelOneRecord(model, note.getPet().getCustomer());
        setModel(model, note.getPet().getCustomer().getId());
        return "customer-module/notes/action";
    }

    @PostMapping("/customer/{customerId}/note")
    public String saveRecord(@PathVariable Long customerId, Note formNote, Model model, RedirectAttributes redirect) {
        Customer customer = customerRepository.findByIdAndMemberId(customerId, AutorisationUtils.getCurrentUserMid()).orElseThrow();

        Pet pet = petRepository.findById(formNote.getPet().getId()).orElseThrow();
        if (!pet.getCustomer().getId().equals(customer.getId())) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/customer/" + customerId + "/reminders";
        }
        if (formNote.isNew()) {
            noteRepository.save(
                    Note.builder().textnote(formNote.getTextnote().trim())
                            .noteDate(formNote.getNoteDate())
                            .pet(pet)
                            .purpose(formNote.getPurpose().trim())
                            .staffMember(formNote.getStaffMember())
                            .build()
            );
        } else {
            noteRepository.save(formNote);
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
        model.addAttribute("petsList", customer.getPets().stream().map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased(), true)).toList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("purposeList", notePurposeLookupRepository.getByMemberId(AutorisationUtils.getCurrentUserMid()).stream().map(rec -> new DoubleText(rec.getPreDefinedPurpose(), rec.getPreDefinedPurpose())).toList());
    }
}