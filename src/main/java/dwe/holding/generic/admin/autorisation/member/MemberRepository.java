package dwe.holding.generic.admin.autorisation.member;

import dwe.holding.generic.admin.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    List<MemberIdNameProjection> findAllProjectedBy();

    Member findByShortCode(String zvs);
}