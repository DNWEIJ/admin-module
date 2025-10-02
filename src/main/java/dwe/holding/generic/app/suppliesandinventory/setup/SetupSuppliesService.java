package dwe.holding.generic.app.suppliesandinventory.setup;

import dwe.holding.generic.admin.autorisation.function_role.FunctionRepository;
import dwe.holding.generic.admin.autorisation.member.MemberRepository;
import dwe.holding.generic.admin.model.Function;
import dwe.holding.generic.admin.model.LocalMember;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.app.suppliesandinventory.model.Distributor;
import dwe.holding.generic.app.suppliesandinventory.model.Supplies;
import dwe.holding.generic.app.suppliesandinventory.repository.DistributorRepository;
import dwe.holding.generic.app.suppliesandinventory.repository.SuppliesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SetupSuppliesService {

    private final SuppliesRepository suppliesRepository;
    private final MemberRepository memberRepository;
    private final DistributorRepository distributorRepository;
    private final FunctionRepository functionRepository;

    public SetupSuppliesService(SuppliesRepository suppliesRepository, MemberRepository memberRepository, DistributorRepository distributorRepository, FunctionRepository functionRepository) {
        this.suppliesRepository = suppliesRepository;
        this.memberRepository = memberRepository;
        this.distributorRepository = distributorRepository;
        this.functionRepository = functionRepository;
    }

    @Transactional
    public void init() {
        if (suppliesRepository.findAll().isEmpty()) {
            Member member = memberRepository.findAll().getFirst();
            UUID memberId = member.getId();
            Set<LocalMember> localMembers = member.getLocalMembers();
            UUID localMemberId = localMembers.stream().findFirst().get().getId();


            List<Function> listFunc = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("DISTRIBUTOR_READ").build(),
                            Function.builder().name("SUPPLIES_READ").build(),

                            Function.builder().name("DISTRIBUTOR_CREATE").build(),
                            Function.builder().name("SUPPLIES_READ").build()
                    )
            );
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