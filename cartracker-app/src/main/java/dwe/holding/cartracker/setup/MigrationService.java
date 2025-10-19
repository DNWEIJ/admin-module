package dwe.holding.cartracker.setup;


import dwe.holding.generic.admin.authorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.cartracker.migration.repository.OldCarRepository;
import dwe.holding.cartracker.migration.repository.OldDriveRepository;
import dwe.holding.cartracker.model.Car;
import dwe.holding.cartracker.model.Trip;
import dwe.holding.cartracker.repository.CarRepository;
import dwe.holding.cartracker.repository.DriveRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated
public class MigrationService {

    private final CarRepository carRepository;
    private final DriveRepository driveRepository;
    private final OldCarRepository oldCarRepository;
    private final OldDriveRepository oldDriveRepository;
    private final MemberRepository memberRepository;

    public MigrationService(CarRepository carRepository, DriveRepository driveRepository, OldCarRepository oldCarRepository, OldDriveRepository oldDriveRepository, MemberRepository memberRepository) {
        this.carRepository = carRepository;
        this.driveRepository = driveRepository;
        this.oldCarRepository = oldCarRepository;
        this.oldDriveRepository = oldDriveRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void init() {

        if (carRepository.count() == 0 && driveRepository.count() == 0) {
            Member member = memberRepository.findByShortCode("CAR");

            List<Car> carList = oldCarRepository.findAll().stream().map(car ->
                    (Car) Car.builder()
                            .name(car.getName())
                            .kmTotal(car.getKmTotal())
                            .kmPerLiter(car.getKmPerLiter())
                            .roadTaxPerYearInCents(car.getRoadTaxPerYearInCents())
                            .insurancePerYearIncents(car.getInsurancePerYearIncents())
                            .memberId(member.getId())
                            .localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
                            .build()
            ).toList();
            carRepository.saveAllAndFlush(carList);

            List<Trip> tripList = oldDriveRepository.findAll().stream().map(trip ->
                    (Trip) Trip.builder()
                            .driveDate(trip.getDriveDate())
                            .carType(trip.getCarType())
                            .person(trip.getPerson())
                            .km(trip.getKm())
                            .kmTotal(trip.getKmTotal())
                            .petrol(trip.isPetrol())
                            .liters(trip.getLiters())
                            .amount(trip.getAmount())
                            .memberId(member.getId())
                            .localMemberId(member.getLocalMembers().stream().findFirst().get().getId())
                            .build()
            ).toList();
            driveRepository.saveAllAndFlush(tripList);
        }
    }
}