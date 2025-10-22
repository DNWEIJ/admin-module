package dwe.holding.generic.admin.authorisation.member;

import dwe.holding.generic.admin.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,   Long> {
    List<MemberIdNameProjection> findAllProjectedBy();

    Member findByShortCode(String shortCode);
}