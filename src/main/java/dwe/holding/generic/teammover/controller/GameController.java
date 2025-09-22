package dwe.holding.generic.teammover.controller;

import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.teammover.model.Driver;
import dwe.holding.generic.teammover.model.Game;
import dwe.holding.generic.teammover.repository.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
public class GameController {
    private final GameRepository gameRepository;

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostMapping("/game")
    String save(@Valid Game game, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "teammover-module/game/action";
        }

        redirect.addFlashAttribute("message", "Wedstrijd opgeslagen!");
        return getRedirectFor(request, processGame(game), "redirect:/game");
    }

    @GetMapping("/game")
    String newScreen(Model model) {
        model.addAttribute("action", "Creer");
        model.addAttribute("game", new Game());
        return "teammover-module/game/action";

    }

    @GetMapping("/game/{id}")
    String showEditScreen(@PathVariable @NotNull UUID id, Model model) {
        model.addAttribute("action", "Bewerk");
        model.addAttribute("game", gameRepository.findById(id).orElseThrow());
        return "teammover-module/game/action";
    }

    @GetMapping("/game/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");

        List<Game> games = gameRepository.findAll(Sort.by(Sort.Direction.ASC, "whenIsTheGame"));
        List<GameSummary> summaries = games.stream()
                .map(game -> {
                    return new GameSummary(
                            game.getId(),
                            game.getWhereIsTheGame(),
                            game.getWhenIsTheGame(),
                            game.getHowManyPeople(),
                            game.getDrivers().stream().mapToInt(Driver::getNrOfTeamMembers).sum(),
                            game.getDrivers().stream().filter(Driver::isAtSwimmingPool).mapToInt(Driver::getNrOfEmptySpots).sum()
                    );

                })
                .toList();
        model.addAttribute("games", summaries);
        return "teammover-module/game/list";
    }

    record GameSummary(UUID id, String whereIsTheGame, LocalDateTime whenIsTheGame, int totalPlayers, int totalPlayersDriver, int totalSeatsDriver) {
    }

    UUID processGame(Game formGame) {

        if (formGame.isNew()) {
            return gameRepository.save(
                    Game.builder()
                            .localMemberId(AutorisationUtils.getCurrentUserMlid())
                            .memberId(AutorisationUtils.getCurrentUserId())
                            .whenIsTheGame(formGame.getWhenIsTheGame())
                            .whereIsTheGame(formGame.getWhereIsTheGame())
                            .howManyPeople(formGame.getHowManyPeople())
                            .build()
            ).getId();
        } else {
            Game game = gameRepository.findById(formGame.getId()).orElseThrow();
            game.setWhenIsTheGame(formGame.getWhenIsTheGame());
            game.setWhereIsTheGame(formGame.getWhereIsTheGame());
            game.setHowManyPeople(formGame.getHowManyPeople());
            return gameRepository.save(game).getId();
        }
    }
}