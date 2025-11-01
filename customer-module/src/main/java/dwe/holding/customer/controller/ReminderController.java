package dwe.holding.customer.controller;

import dwe.holding.customer.model.Customer;
import dwe.holding.customer.model.Pet;
import dwe.holding.customer.model.Reminder;
import dwe.holding.customer.repository.PetRepository;
import dwe.holding.customer.repository.ReminderRepository;
import dwe.holding.generic.shared.model.frontend.PresentationElement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
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
public class ReminderController {
    private final ReminderRepository reminderRepository;
    private final PetRepository petRepository;
    private final ValidateCustomer validateCustomer;
    private final PersistenceManagedTypes persistenceManagedTypes;

    public ReminderController(ReminderRepository reminderRepository, PetRepository petRepository, ValidateCustomer validateCustomer, PersistenceManagedTypes persistenceManagedTypes) {
        this.reminderRepository = reminderRepository;

        this.petRepository = petRepository;
        this.validateCustomer = validateCustomer;
        this.persistenceManagedTypes = persistenceManagedTypes;
    }

    @GetMapping("/customer/{customerId}/reminders")
    String list(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        model.addAttribute("reminders", reminderRepository.getByCustomerId(customerId));
        return "customer-module/reminder/list";
    }

    @GetMapping("/customer/{customerId}/reminder")
    String newRecord(@PathVariable Long customerId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        model.addAttribute("reminder", Reminder.builder().pet(new Pet()).build());
        model.addAttribute("petsList",
                petRepository.findByCustomer_IdOrderByDeceasedAsc(customerId)
                        .stream().map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased(), true)).toList()
        );
        model.addAttribute("costingReminderList", "");
//      <query name="costingReminders">
//          select new  nl.achtiiacht.framework.util.ReferenceList(cst.id, cst.rnomenclature)
//          from Costing as cst where cst.mid = ? and cst.rnomenclature is not null  and cst.rnomenclature is not empty
//          group by cst.rnomenclature order by cst.rnomenclature desc
//      </query>
        return "customer-module/reminder/action";
    }


    @PostMapping("/customer/{customerId}/reminder")
    String saveRecord(@Valid Reminder formReminder, @PathVariable Long customerId, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        Pet pet = petRepository.findById(formReminder.getPet().getId()).orElseThrow();
        if (!pet.getCustomer().getId().equals(customerId)) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/customer/customer/" + customerId + "/reminders";
        }

        if (formReminder.isNew()) {
            reminderRepository.save(
                    Reminder.builder().reminder(formReminder.getReminder())
                            .pet(pet)
                            .dueDate(formReminder.getDueDate())
                            .build()
            );
        } else {
            formReminder.setOriginatingAppointmentId(-1L);
            Reminder savedReminder = reminderRepository.save(formReminder);
            pet.getReminders().add(savedReminder);
        }
        return "redirect:/customer/customer/" + customerId + "/reminders";
    }

    @GetMapping("/customer/{customerId}/reminder/{reminderId}")
    @Transactional
    String editRecord(@PathVariable Long customerId, @PathVariable Long reminderId, Model model, RedirectAttributes redirect) {
        if (validateCustomer.isInvalid(customerId, redirect)) return "redirect:/customer/customer";
        Reminder reminder = reminderRepository.findById(reminderId).orElseThrow();
        model.addAttribute("reminder", reminder.getPet().getCustomer().getId().equals(customerId) ? reminder : new Reminder());
        setModel(model, reminder.getPet().getCustomer());
        return "customer-module/reminder/action";
    }

    void setModel(Model model, Customer customer) {
        model.addAttribute("petsList", customer.getPets());
        model.addAttribute("activeMenu", "reminders");
        model.addAttribute("customerId", customer.getId());
    }

    public record FormReminder(Long petId, Reminder reminder) {
    }
}