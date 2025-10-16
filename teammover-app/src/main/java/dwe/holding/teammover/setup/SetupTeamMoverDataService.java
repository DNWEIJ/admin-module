package dwe.holding.teammover.setup;

import dwe.holding.generic.admin.authorisation.member.LocalMemberRepository;
import dwe.holding.generic.admin.authorisation.member.MemberRepository;
import dwe.holding.teammover.model.Game;
import dwe.holding.teammover.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
  

@Service
@Slf4j
public class SetupTeamMoverDataService {

    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final LocalMemberRepository localmemberRepository;

    public SetupTeamMoverDataService(GameRepository gameRepository, MemberRepository memberRepository, LocalMemberRepository localmemberRepository) {
        this.gameRepository = gameRepository;
        this.memberRepository = memberRepository;
        this.localmemberRepository = localmemberRepository;
    }

    record GameInfo(LocalDateTime DatumTijd, String wedstrijd, String locatie) {
    }

    @Transactional
    public void init() {

        if (gameRepository.findAll().isEmpty()) {
            log.info("MigrationTeamMoverDataService:: member");
              Long memberId = memberRepository.findByShortCode("ZVS").getId();
              Long localMemberId = localmemberRepository.findByLocalMemberName("GO12-2").getId();

            log.info("MigrationTeamMoverAdminService:: user");
            List<GameInfo> sassenheimGames = List.of(
                    new GameInfo(LocalDateTime.of(2025, 9, 27, 18, 15), "WZK Zwemmen GO12-1 - Sassenheim GO12-2 *", "Sterrenbad in Wassenaar"),
                    new GameInfo(LocalDateTime.of(2025, 10, 5, 16, 0), "Sassenheim GO12-2 * - Z&PC DE Gpuwe GO12-3 *", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2025, 10, 11, 15, 15), "Oceanus GO12-1* - Sassenheim GO12-2 *", "De Waterlelie in Aalsmeer"),
                    new GameInfo(LocalDateTime.of(2025, 10, 18, 17, 0), "Sassenheim GO12-2 * - Vivax GO12-2", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2025, 11, 1, 17, 0), "Sassenheim GO12-2 * - de Amstel/de Snippen GO12-1", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2025, 11, 29, 17, 15), "Alkemade GO12-1 * - Sassenheim GO12-2 *", "De Tweesprong in Roelofarendsveen"),
                    new GameInfo(LocalDateTime.of(2025, 12, 13, 17, 0), "Sassenheim GO12-2 * - Z&PC Aquadraat GO12-1", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2026, 1, 10, 16, 45), "Z&PC DE Gpuwe GO12-3 * - Sassenheim GO12-2 *", "De Sniep in Waddinxveen"),
                    new GameInfo(LocalDateTime.of(2026, 1, 17, 17, 0), "Sassenheim GO12-2 * - WZK Zwemmen GO12-1", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2026, 1, 31, 16, 15), "De Plas GO12-2 * - Sassenheim GO12-2 *", "De Wel in Nieuwkoop"),
                    new GameInfo(LocalDateTime.of(2026, 2, 8, 16, 0), "Sassenheim GO12-2 * - Oceanus GO12-1*", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2026, 3, 7, 18, 15), "de Amstel/de Snippen GO12-1 - Sassenheim GO12-2 *", "Het Veenweidebad in Mijdrecht"),
                    new GameInfo(LocalDateTime.of(2026, 4, 12, 16, 0), "Sassenheim GO12-2 * - Alkemade GO12-1 *", "Wasbeek in Sassenheim"),
                    new GameInfo(LocalDateTime.of(2026, 4, 18, 16, 30), "Vivax GO12-2 - Sassenheim GO12-2 *", "Poelmeer in Oegstgeest"),
                    new GameInfo(LocalDateTime.of(2026, 4, 25, 15, 45), "Z&PC Aquadraat GO12-1 - Sassenheim GO12-2 *", "De Does in Leiderdorp")
            );
            Game baseGame = Game.builder().localMemberId(localMemberId).memberId(memberId).build();

            gameRepository.saveAllAndFlush(
                    sassenheimGames.stream()
                            .map(info -> (Game) baseGame.toBuilder()
                                    .whereIsTheGame(info.locatie)
                                    .whenIsTheGame(info.DatumTijd)
                                    .doWeNeedToDrive(!info.locatie.contains("Wasbeek"))
                                    .howManyPeople(0)
                                    .build()
                            ).toList()
            );
        }
    }
}