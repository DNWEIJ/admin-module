package dwe.holding.admin.authorisation.notenant.function;

import dwe.holding.admin.authorisation.notenant.projection.IdName;
import dwe.holding.admin.model.notenant.Function;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface FunctionRepository extends JpaRepository<Function, Long> {

//    @Query(nativeQuery = true, value =
//            """
//    select f.* from admin_function f
//    join admin_function_role fr on f.id = fr.function_id
//    join admin_role r on  fr.role_id = r.id
//    join admin_user_role ur on ur.role_id = r.id
//    join admin_user u on ur.user_id = u.id
//    where u.id = :userId
//    """
//    )
//    List<Function> getAllFunctionsForUser(@Param("userId") Long userId);


    @Cacheable("functions")
    @Query("SELECT new dwe.holding.admin.authorisation.notenant.projection.IdName(f.id, f.name) FROM Function f")
    List<IdName> findAllCachedNames();



    Optional<Function> findByName(String sellRead);
}