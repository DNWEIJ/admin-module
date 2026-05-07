package dwe.holding.admin.authorisation.notenant.function;

import dwe.holding.admin.authorisation.notenant.function_role.InternalFunctionRoleRepository;
import dwe.holding.admin.model.notenant.FunctionRole;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class FunctionCacheService {
    @Autowired
    private InternalFunctionRoleRepository functionRoleRepository;

    private volatile Map<Long, List<Long>> functionToRoles;
    private volatile boolean timePassed = true;

    private final java.util.concurrent.ScheduledExecutorService executor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();

    // this is a really simple cache, to ensure we do not call for EVERY uri the db to get mapping function-role
    @PostConstruct
    void init() {
        executor.scheduleAtFixedRate(() -> timePassed = true, 60, 60, java.util.concurrent.TimeUnit.SECONDS);
    }

    public List<Long> findRolesByFunctionId(Long functionId) {
        if (timePassed || functionToRoles == null) {
            reload();
        }
        return functionToRoles.get(functionId);
    }

    private synchronized void reload() {
        functionToRoles = functionRoleRepository.findAll().stream()
                .collect(Collectors.groupingBy(FunctionRole::getFunctionId, Collectors.mapping(FunctionRole::getRoleId, Collectors.toList()))
                );
        timePassed = false;
    }
}


