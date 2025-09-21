package dwe.holding.generic.migration;

import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.LocalMember;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.suppliesandinventory.model.Distributor;
import dwe.holding.generic.suppliesandinventory.model.Supplies;
import dwe.holding.generic.suppliesandinventory.repository.DistributorRepository;
import dwe.holding.generic.suppliesandinventory.repository.SuppliesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MigrationSuppliesService {

    private final SuppliesRepository suppliesRepository;
    private final MemberRepository memberRepository;
    private final DistributorRepository distributorRepository;

    public MigrationSuppliesService(SuppliesRepository suppliesRepository, MemberRepository memberRepository, DistributorRepository distributorRepository) {
        this.suppliesRepository = suppliesRepository;
        this.memberRepository = memberRepository;
        this.distributorRepository = distributorRepository;
    }

    @Transactional
    public void init() {
        if (suppliesRepository.findAll().isEmpty()) {
            Member member = memberRepository.findAll().getFirst();
            Long memberId = member.getId();
            Set<LocalMember> localMembers = member.getLocalMembers();
            Long localMemberId = localMembers.stream().findFirst().get().getId();

            List<Distributor> distributors = distributorRepository.saveAllAndFlush(
                    List.of(
                            Distributor.builder().distributorName("Distributor One").memberId(memberId).localMemberId(localMemberId).build(),
                            Distributor.builder().distributorName("Distributor Two").memberId(memberId).localMemberId(localMemberId).build(),
                            Distributor.builder().distributorName("Distributor Three").memberId(memberId).localMemberId(localMemberId).build()
                    )
            );

            suppliesRepository.saveAllAndFlush(
                    List.of(
                            Supplies.builder().nomenclature("Supply One").memberId(memberId).localMemberId(localMemberId).distributor(distributors.get(0)).build(),
                            Supplies.builder().nomenclature("Supply Two").memberId(memberId).localMemberId(localMemberId).build(),
                            Supplies.builder().nomenclature("Supply Three").memberId(memberId).localMemberId(localMemberId).distributor(distributors.get(2)).build(),
                            Supplies.builder().nomenclature("Supply Four").memberId(memberId).localMemberId(localMemberId).build()
                    )
            );
        }
    }
}