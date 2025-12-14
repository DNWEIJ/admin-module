package dwe.holding.salesconsult.setup;

import dwe.holding.admin.authorisation.notenant.function_role.FunctionRoleRepository;
import dwe.holding.admin.authorisation.tenant.role.FunctionRepository;
import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.notenant.FunctionRole;
import dwe.holding.admin.model.tenant.Role;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SetupSalesConsultService {

    private final FunctionRepository functionRepository;
    private final RoleRepository roleRepository;
    private final FunctionRoleRepository functionRoleRepository;

    public SetupSalesConsultService( FunctionRepository functionRepository, RoleRepository roleRepository, FunctionRoleRepository functionRoleRepository) {
        this.functionRepository = functionRepository;
        this.roleRepository = roleRepository;
        this.functionRoleRepository = functionRoleRepository;
    }

    @Transactional
    public   Long init() {
        if (functionRepository.findByName("sell_READ")) {
            log.info("MigrationSalesConsultService:: general functions for SALES role");
            List<Function> listFuncRead = functionRepository.saveAllAndFlush(
                    List.of(
                            Function.builder().name("SELL_READ").build(),
                            Function.builder().name("SELL").build()
                    ));


            log.info("MigrationSalesConsultService:: role");
            List<Role> listRole = roleRepository.saveAllAndFlush(
                    List.of(
                            Role.builder().name("SALESCONSULT_READ").memberId(77L).build(),
                            Role.builder().name("SALESCONSULT_CREATE").memberId(77L).build()
                    )
            );
            log.info("MigrationSalesConsultService:: start creating the connection between function and role..");
            Role roleSuperAdmin = listRole.stream().filter(r -> r.getName().equals("SALESCONSULT_READ")).findFirst().get();
            functionRoleRepository.saveAllAndFlush(
                    listFuncRead.stream()
                            .map(func -> (FunctionRole) FunctionRole.builder().functionId(func.getId()).roleId(roleSuperAdmin.getId()).build())
                            .toList()
            );
            return 77L;
        }
        return null;
    }
}