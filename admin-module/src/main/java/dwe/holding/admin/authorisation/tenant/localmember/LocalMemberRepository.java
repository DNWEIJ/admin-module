package dwe.holding.admin.authorisation.tenant.localmember;

import dwe.holding.admin.model.tenant.LocalMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocalMemberRepository extends JpaRepository<LocalMember,   Long> {

    LocalMember findByLocalMemberName(String go12);

    List<LocalMember> findByMemberId(Long id);
}