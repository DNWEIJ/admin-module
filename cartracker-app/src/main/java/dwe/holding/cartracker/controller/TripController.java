package dwe.holding.cartracker.controller;

import dwe.holding.cartracker.model.Trip;
import dwe.holding.cartracker.service.CarService;
import dwe.holding.cartracker.service.DriveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;


@Controller
@RequestMapping(path = "/cartracker")
class TripController {

    private final String[] gifies = {
            "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExZTI5N2Z1cmI4aXdvdWNoazhrcHVva25tdWUyeG54NDAycWFxYzRtcCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/xNBcChLQt7s9a/giphy.gif",
            "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMW5rdXNtNmp1NHc1NG4zOWFqcnRienhiaDBkbGV4eTAyYzl0OWRvMSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/13wQVrbvz2nyG4/giphy.gif",
            "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExbHc1bG9maHNsc215bXFpamE5bjl6dGk0czdyeGRheWF0dmU5YzdzMCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/1dagNhv8Oqu6l8U3ZK/giphy.gif",
            "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExZjBvMmFzY2Z3d3dicGpxam9tYWs4NGwzcDZscjV2Z3lxazBkMGo4YSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/xT9Iglw135UyrrzjB6/giphy.gif",
            "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExcTB4eHVnYzl4cnF3Z292cGZpdzFvMHRiNzQzZWRkdjB1cWxlemQzdCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/lN9Mm4dMyDdu16hIqQ/giphy.gif"
        };
    private int counter = 0;

    @PostMapping("/trip")
    String saveCarRecord(Trip drive, RedirectAttributes redirect) {
        if (drive.isValid()) {
            Long id = driveService.saveRecordForPaid(drive);
            carService.saveRecord(drive);

            redirect.addFlashAttribute("successAction", driveService.getHtmlStringOf(id));

            return "redirect:/cartracker/success";
        } else {
            return "redirect:/cartracker/error";
        }
    }


    @GetMapping("/trip")
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

    @GetMapping("/success")
    String getSuccess(Model model) {
        model.addAttribute("imagesrc", gifies[counter++ % 5]);
        return "cartracker-module/success.html";
    }
}