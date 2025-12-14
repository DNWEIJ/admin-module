package dwe.holding.supplyinventory.setup;

import dwe.holding.admin.authorisation.notenant.function_role.FunctionRoleRepository;
import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.role.FunctionRepository;
import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRoleRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserRole;
import dwe.holding.supplyinventory.model.Distributor;
import dwe.holding.supplyinventory.model.Supplies;
import dwe.holding.supplyinventory.repository.DistributorRepository;
import dwe.holding.supplyinventory.repository.SuppliesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SetupSuppliesService {

    private final SuppliesRepository suppliesRepository;
    private final MemberRepository memberRepository;
    private final DistributorRepository distributorRepository;
    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository localMemberRepository;

    public SetupSuppliesService(SuppliesRepository suppliesRepository, MemberRepository memberRepository, DistributorRepository distributorRepository, FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository, UserRoleRepository userRoleRepository, UserRepository localMemberRepository) {
        this.suppliesRepository = suppliesRepository;
        this.memberRepository = memberRepository;
        this.distributorRepository = distributorRepository;
        this.functionRepository = functionRepository;
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
        this.userRoleRepository = userRoleRepository;
        this.localMemberRepository = localMemberRepository;
    }

    @Transactional
    public void init() {
        if (suppliesRepository.findAll().isEmpty()) {

              Long memberId = memberRepository.findByShortCode("DWE").getId();

            List<Function> listFunc = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("DISTRIBUTOR_READ").build(),
                            Function.builder().name("SUPPLIES_READ").build(),

                            Function.builder().name("DISTRIBUTOR_CREATE").build(),
                            Function.builder().name("SUPPLIES_CREATE").build()
                    )
            );

            List<Role> listRoles = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("SUPPLIES").memberId(memberId).build(),
                            Role.builder().name("PRICING").memberId(memberId).build(),
                            Role.builder().name("INVENTORY").memberId(memberId).build()
                    )
            );

            Role roleSupplies = listRoles.stream().filter(r -> r.getName().equals("SUPPLIES")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFunc.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleSupplies.getId()).build())
                            .toList()
            );

            User user = localMemberRepository.findByAccount("daniel").stream().filter(u -> u.getMember().getId().equals(memberId)).findFirst().orElseThrow();
            userRoleRepository.saveAllAndFlush(
                    List.of(
                            UserRole.builder().role(roleSupplies).user(user).build()
//                            UserRole.builder().role(roleAdmin).user(user).build(),
//                            UserRole.builder().role(roleDefault).user(user).build()
                    )
            );


            //***********//
            // DATA PART //
            //***********//
            List<Distributor> savedDistributors = distributorRepository.saveAllAndFlush(
                    List.of(
                            Distributor.builder().distributorName("Distributor One").memberId(memberId).build(),
                            Distributor.builder().distributorName("Distributor Two").memberId(memberId).build(),
                            Distributor.builder().distributorName("Distributor Three").memberId(memberId).build()
                    )
            );

            suppliesRepository.saveAllAndFlush(
                    List.of(
                            Supplies.builder().nomenclature("Supply One").memberId(memberId).distributor(savedDistributors.get(0)).build(),
                            Supplies.builder().nomenclature("Supply Two").memberId(memberId).build(),
                            Supplies.builder().nomenclature("Supply Three").memberId(memberId).distributor(savedDistributors.get(2)).build(),
                            Supplies.builder().nomenclature("Supply Four").memberId(memberId).build()
                    )
            );
        }
    }
}