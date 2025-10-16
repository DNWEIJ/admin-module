package dwe.holding.cartracker.controller;

import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.cartracker.model.Trip;
import dwe.holding.cartracker.service.CarService;
import dwe.holding.cartracker.service.DriveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
  

@Controller
class TripController {


    private final String[] gifies = {
            "https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExbjZzNnl4Z2t6NGxuZnRkandheDB0NjhtbWVvazR3OWd2MWZhbTZwNiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/XreQmk7ETCak0/giphy.gif",
            "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExZTJxcHllcDcwejRvYmFza2xxNHE5NTBwOGl2eWVmdjNsNm53bHhpcCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/7kO9VZjCv3FkI/giphy.gif",
            "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExZ2lyOXlpbTJnd3Y0Y2VoeTc5d3NibTY2NXBrZjVmb3l5c3I4aWhjbCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/1GTZA4flUzQI0/giphy.gif",
            "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExNzR0cTQwdzE2bWdnbDQzeWYzaXJ1Y2N2ZnBqdHJqeHFzYXJjMHViNCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/CvZuv5m5cKl8c/giphy.gif",
            "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExbzV3MHEwZWt6eG5oOTY0ODJpd2FheTJvMWFnbHVoaDhwbnNxODU2eiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/8xSnw21AM7OQo/giphy.gif"
    };
    private int counter = 0;

    @PostMapping("/cartracker/trip")
    String saveCarRecord(Trip drive, RedirectAttributes redirect) {
        if (drive.isValid()) {
            drive.setMemberId(AutorisationUtils.getCurrentUserMid());
            drive.setLocalMemberId(AutorisationUtils.getCurrentUserMid());
              Long id = driveService.saveRecord(drive);
            carService.saveRecord(drive);

            redirect.addFlashAttribute("successAction", driveService.getHtmlStringOf(id));

            return "redirect:/cartracker/success";
        } else {
            return "redirect:/error";
        }
    }


    @GetMapping("/cartracker/trip")
    String getCarRecord(Model model) {
        model.addAttribute("carTypes", carService.getAllNames());
        model.addAttribute("carsPreviousTotal", carService.getAllNameAndTotalKm());
        return "cartracker-module/trip";
    }


    // ***************
// plumbing
// ***************
    final DriveService driveService;
    final CarService carService;

    TripController(DriveService driveService, CarService carService) {
        this.driveService = driveService;
        this.carService = carService;
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleNotFound(IllegalArgumentException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        model.addAttribute("person", authentication.getName());
        model.addAttribute("role", ((User) authentication.getPrincipal()).getAuthorities().toString());
    }

    @GetMapping("/cartracker/success")
    String getSuccess(Model model) {
        model.addAttribute("imagesrc", gifies[counter++ % 5]);
        return "cartracker-module/success.html";
    }
}