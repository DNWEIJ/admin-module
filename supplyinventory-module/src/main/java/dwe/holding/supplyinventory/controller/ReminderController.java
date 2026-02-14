package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.model.Pet;
import dwe.holding.customer.client.model.Reminder;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.supplyinventory.repository.CostingRepository;
import dwe.holding.supplyinventory.repository.ReminderRepository;
import jakarta.transaction.Transactional;
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

import java.time.LocalDate;

@Controller
@RequestMapping(path = "/supplies")
@Slf4j
@AllArgsConstructor
public class ReminderController {
    private final ReminderRepository reminderRepository;
    private final PetRepository petRepository;
    private final CostingRepository costingRepository;
    private final CustomerService customerService;

    @GetMapping("/customer/{customerId}/reminders")
    String list(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("reminders", reminderRepository.findByPet_Customer_IdOrderByDueDateDesc(customer.id()));
        return "supplies-module/reminder/list";
    }

    @GetMapping("/customer/{customerId}/reminder")
    String newRecord(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("reminder", Reminder.builder().dueDate(LocalDate.now()).pet(new Pet()).build());
        setModel(model, customer);
        return "supplies-module/reminder/action";
    }


    @PostMapping("/customer/{customerId}/reminder")
    String saveRecord(@Valid Reminder formReminder, @PathVariable Long customerId, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Pet pet = petRepository.findById(formReminder.getPet().getId()).orElseThrow();
        if (!pet.getCustomer().getId().equals(customer.id())) {
            redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            return "redirect:/supplies/customer/" + customerId + "/reminders";
        }

        if (formReminder.isNew()) {
            reminderRepository.save(
                    Reminder.builder()
                            .reminderText(formReminder.getReminderText())
                            .pet(pet).dueDate(formReminder.getDueDate())
                            .build()
            );
        } else {
            formReminder.setOriginatingAppointmentId(-1L);
            Reminder savedReminder = reminderRepository.save(formReminder);
            pet.getReminders().add(savedReminder);
        }
        return "redirect:/supplies/customer/" + customerId + "/reminders";
    }

    @GetMapping("/customer/{customerId}/reminder/{reminderId}")
    @Transactional
    String editRecord(@PathVariable Long customerId, @PathVariable Long reminderId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("reminder", reminderRepository.findById(reminderId).orElseThrow());
        setModel(model, customer);
        return "supplies-module/reminder/action";
    }

    void setModel(Model model, CustomerService.Customer customer) {
        model
                .addAttribute("reminders", reminderRepository.findByPet_Customer_IdOrderByDueDateDesc(customer.id()))

                .addAttribute("petsList", petRepository.findByCustomer_IdOrderByDeceasedAsc(customer.id())
                        .stream().map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased(), true)).toList()
                )
                .addAttribute("activeMenu", "reminders")
                .addAttribute("customerId", customer.id())
                .addAttribute("costingReminderList",
                        costingRepository.findWithNonEmptyReminderNomenclature(AutorisationUtils.getCurrentUserMid())
                                .stream().map(reminderNomenclature -> new PresentationElement(reminderNomenclature, reminderNomenclature)).toList()
                );
    }
}