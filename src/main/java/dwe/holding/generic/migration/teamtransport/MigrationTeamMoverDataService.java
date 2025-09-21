package dwe.holding.generic.migration.teamtransport;

import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.teammover.model.Driver;
import dwe.holding.generic.teammover.model.Game;
import dwe.holding.generic.teammover.repository.DriverRepository;
import dwe.holding.generic.teammover.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MigrationTeamMoverDataService {

    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final DriverRepository driverRepository;

    public MigrationTeamMoverDataService(GameRepository gameRepository, MemberRepository memberRepository, DriverRepository driverRepository) {
        this.gameRepository = gameRepository;
        this.memberRepository = memberRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public void init() {
        log.info("MigrationTeamMoverDataService:: member");
        if (gameRepository.findAll().isEmpty()) {
            Member member = memberRepository.findAll().getFirst();
            UUID memberId = member.getId();
            log.info("MigrationTeamMoverDataService:: member");
            List<Game> games = gameRepository.saveAll(
                    List.of(
                            Game.builder().memberId(memberId).localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
                                    .howManyPeople(10)
                                    .doWeNeedToDrive(false)
                                    .whereIsTheGame("Sassenheim")
                                    .whenIsTheGame(java.time.LocalDateTime.now())
                                    //                                  .drivers(drivers)
                                    .build(),
                            Game.builder().memberId(memberId).localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
                                    .howManyPeople(10)
                                    .doWeNeedToDrive(true)
                                    .whereIsTheGame("Aalsmeer")
                                    .whenIsTheGame(java.time.LocalDateTime.now())
//                                    .drivers(drivers)
                                    .build()
                    )
            );
            log.info("MigrationTeamMoverDataService:: driver");
            List<Driver> drivers = driverRepository.saveAll(
                    List.of(
                            Driver.builder().memberId(memberId).localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
                                    .accountName("jeroen")
                                    .atSwimmingPool(false)
                                    .nrOfEmptySpots(2)
                                    .nrOfTeamMembers(2)
                                    .game(games.get(0))
                                    .build(),
                            Driver.builder().memberId(memberId).localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
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