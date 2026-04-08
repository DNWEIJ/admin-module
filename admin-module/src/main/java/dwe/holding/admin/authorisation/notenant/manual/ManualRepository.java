package dwe.holding.admin.authorisation.notenant.manual;

import dwe.holding.admin.model.notenant.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {
    Optional<Manual> findByName(String path);
}
