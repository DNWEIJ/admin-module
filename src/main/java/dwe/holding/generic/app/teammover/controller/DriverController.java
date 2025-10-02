package dwe.holding.generic.app.teammover.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dwe.holding.generic.admin.model.type.DriveOptionEnum;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.app.teammover.model.Driver;
import dwe.holding.generic.app.teammover.model.Game;
import dwe.holding.generic.app.teammover.model.TeamMoverUserPreferences;
import dwe.holding.generic.app.teammover.repository.DriverRepository;
import dwe.holding.generic.app.teammover.repository.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@Validated
public class DriverController {
    private final GameRepository gameRepository;
    private final DriverRepository driverRepository;
    private final ObjectMapper objectMapper;

    public DriverController(GameRepository gameRepository, DriverRepository driverRepository, ObjectMapper objectMapper) {
        this.gameRepository = gameRepository;
        this.driverRepository = driverRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/teammover/game/{id}/driver")
    String save(@Valid Driver formDriver, BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "teammover-module/driver/action";
        }
        processDriver(formDriver);
        redirect.addFlashAttribute("message", "Rit opgeslagen!");
        return "redirect:/teammover/game/list";
    }


    @GetMapping("/teammover/game/{id}/driver")
    String showEditScreen(@PathVariable @NotNull UUID id, Model model) {
        model.addAttribute("action", "Bewerk");
        Game game = gameRepository.findById(id).orElseThrow();
        model.addAttribute("game", game);
        model.addAttribute("driveOptionList", DriveOptionEnum.getWebList());
        model.addAttribute("driver",
                game.getDrivers().isEmpty() ?
                        newDriver(game.getId()) :
                        game.getDrivers().stream()
                                .filter(driver -> driver.getAccountName().equalsIgnoreCase(AutorisationUtils.getCurrentUserAccount()))
                                .findFirst()
                                .orElse(newDriver(game.getId()))
        );
        return "teammover-module/driver/action";
    }

    Driver newDriver(UUID gameId) {
        return Driver.builder()
                .accountName(AutorisationUtils.getCurrentUserAccount())
                .game(Game.builder().id(gameId).build())
                .nrOfTeamMembers(objectMapper.convertValue(AutorisationUtils.getCurrentUserPref(), TeamMoverUserPreferences.class).getNrOfTeamMembers())
                .driveOption(DriveOptionEnum.Not)
                .build();
    }

    UUID processDriver(Driver formDriver) {
        Game game = gameRepository.findById(formDriver.getGame().getId()).orElseThrow();
        if (formDriver.isNew()) {
            Driver savedGame = driverRepository.save(
                    Driver.builder()
                            .accountName(AutorisationUtils.getCurrentUserAccount())
                            .nrOfTeamMembers(formDriver.getNrOfTeamMembers())
                            .nrOfEmptySpots(formDriver.getNrOfEmptySpots())
                            .driveOption(formDriver.getDriveOption())
                            .localMemberId(AutorisationUtils.getCurrentUserMlid())
                            .memberId(AutorisationUtils.getCurrentUserId())
                            .game(game)
                            .build()
            );
            game.getDrivers().add(savedGame);
            return game.getDrivers().stream()
                    .filter(driver -> driver.getAccountName().equalsIgnoreCase(AutorisationUtils.getCurrentUserAccount()))
                    .findFirst().orElseThrow().getId();

        } else {
            Driver dbDriver = game.getDrivers().stream().filter(driver -> driver.getId().equals(formDriver.getId())).findFirst().orElseThrow();
            dbDriver.setNrOfEmptySpots(formDriver.getNrOfEmptySpots());
            dbDriver.setNrOfTeamMembers(formDriver.getNrOfTeamMembers());
            dbDriver.setDriveOption(formDriver.getDriveOption());
            Driver savedDriver = driverRepository.save(dbDriver);
            return savedDriver.getId();
        }

    }
}