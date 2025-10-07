package dwe.holding.generic.admin.authorisation.member;

import dwe.holding.generic.admin.model.LocalMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocalMemberRepository extends JpaRepository<LocalMember, UUID> {

    List<LocalMember> findByMember_Id(UUID memberId);

    LocalMember findByLocalMemberName(String go12);
}