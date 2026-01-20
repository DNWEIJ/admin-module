package dwe.holding.salesconsult.consult.controller;

import dwe.holding.customer.client.controller.NoteController;
import dwe.holding.customer.client.model.Note;
import dwe.holding.shared.model.frontend.PresentationElement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxNoteController {
    private final NoteController noteController;

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/note")
    String returnNoteModal(@PathVariable Long customerId, @PathVariable Long petId, Model model) {
        noteController.newNote(customerId, model);

        List<PresentationElement> pets = (List<PresentationElement>) model.getAttribute("petsList");
        PresentationElement pet = pets.stream().filter(p -> !p.getId().equals(petId)).findFirst().orElseThrow();
        Note note = (Note) model.getAttribute("note");
        note.getPet().setId(petId);
        return "sales-module/fragments/htmx/notemodal";
    }

    @PostMapping("/visit/customer/{customerId}/pet/{petId}/note")
    String saveNoteModal(@PathVariable Long customerId, @PathVariable Long petId, @ModelAttribute Note note, Model model, RedirectAttributes redirect, HttpServletResponse response) {
        noteController.saveRecord(customerId, note, model, redirect);
        if (redirect.getFlashAttributes().containsKey("message")) {
            model.addAttribute("message", redirect.getFlashAttributes().get("message"));
        } else {
            model.addAttribute("message", "Note saved successfully");
        }
        // TODO: On Close, first update the message to show "succesfull"then close the modal
        response.setHeader("HX-Trigger", "closeModal");
        return "fragments/elements/empty";
    }
}