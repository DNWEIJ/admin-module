package dwe.holding.generic.migration.teamtransport;

import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.teammover.model.Driver;
import dwe.holding.generic.teammover.model.Game;
import dwe.holding.generic.teammover.repository.DriverRepository;
import dwe.holding.generic.teammover.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MigrationTeamMoverService {

    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final DriverRepository driverRepository;

    public MigrationTeamMoverService(GameRepository gameRepository, MemberRepository memberRepository, DriverRepository driverRepository) {
        this.gameRepository = gameRepository;
        this.memberRepository = memberRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public void init() {
        if (gameRepository.findAll().isEmpty()) {
            Member member = memberRepository.findAll().getFirst();
            Long memberId = member.getId();

            List<Game> games = gameRepository.saveAll(
                    List.of(
                            Game.builder().memberId(memberId)
                                    .howManyPeople(10)
                                    .doWeNeedToDrive(false)
                                    .whereIsTheGame("Sassenheim")
                                    .whenIsTheGame(java.time.LocalDateTime.now())
  //                                  .drivers(drivers)
                                    .build(),
                            Game.builder().memberId(memberId)
                                    .howManyPeople(10)
                                    .doWeNeedToDrive(true)
                                    .whereIsTheGame("Aalsmeer")
                                    .whenIsTheGame(java.time.LocalDateTime.now())
//                                    .drivers(drivers)
                                    .build()
                    )
            );

            List<Driver> drivers = driverRepository.saveAll(
                    List.of(
                            Driver.builder().memberId(memberId)
                                    .accountName("jeroen")
                                    .atSwimmingPool(false)
                                    .nrOfEmptySpots(2)
                                    .nrOfTeamMembers(2)
                                    .game(games.get(0))
                                    .build(),
                            Driver.builder().memberId(memberId)
                                    .accountName("daan")
                                    .atSwimmingPool(false)
                                    .nrOfEmptySpots(2)
                                    .nrOfTeamMembers(2)
                                    .game(games.get(0))
                                    .build()
                    )
            );
            games.get(0).setDrivers(drivers);
            gameRepository.save(games.get(0));
        }
    }
}