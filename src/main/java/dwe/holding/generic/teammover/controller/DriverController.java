package dwe.holding.generic.teammover.controller;

import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.teammover.model.Driver;
import dwe.holding.generic.teammover.model.Game;
import dwe.holding.generic.teammover.repository.DriverRepository;
import dwe.holding.generic.teammover.repository.GameRepository;
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

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;


@Controller
@Validated
public class DriverController {
    private final GameRepository gameRepository;
    private final DriverRepository driverRepository;

    public DriverController(GameRepository gameRepository, DriverRepository driverRepository) {
        this.gameRepository = gameRepository;
        this.driverRepository = driverRepository;
    }

    @PostMapping("/driver")
    String save(@Valid Driver driver, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "teammover-module/driver/action";
        }
        driver.setMemberId(AutorisationUtils.getCurrentUserMid());
        Driver savedDriver = driverRepository.save(driver);
        Game game = gameRepository.findById(Long.parseLong(request.getParameter("gameId"))).orElseThrow();
        game.getDrivers().add(savedDriver);
        gameRepository.save(game);

        redirect.addFlashAttribute("message", "Rit opgeslagen!");
        return getRedirectFor(request, savedDriver.getId(), "redirect:/game/list");
    }

    @GetMapping("/driver")
    String newScreen(Model model) {
        model.addAttribute("action", "Creer");
        model.addAttribute("driver", newDriver());
        return "teammover-module/driver/action";

    }

    @GetMapping("/driver/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Bewerk");
        Game game = gameRepository.findById(id).orElseThrow();
        model.addAttribute("game", game);
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("driver",
                game.getDrivers().isEmpty() ? newDriver() :
                        game.getDrivers().stream()
                                .filter(driver -> driver.getAccountName().equalsIgnoreCase(AutorisationUtils.getCurrentUserAccount())).findFirst().orElse(new Driver())
        );
        model.addAttribute("driverList", game.getDrivers() == null ? null : game.getDrivers().stream());
        return "teammover-module/driver/action";
    }

    @GetMapping("/driver/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("games", gameRepository.findAll());
        return "teammover-module/driver/list";
    }

    Driver newDriver() {
        return Driver.builder().accountName(AutorisationUtils.getCurrentUserAccount()).build();
    }
}