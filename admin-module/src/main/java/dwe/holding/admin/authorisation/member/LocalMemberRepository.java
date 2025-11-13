package dwe.holding.admin.authorisation.member;

import dwe.holding.admin.model.LocalMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocalMemberRepository extends JpaRepository<LocalMember,   Long> {

    List<LocalMember> findByMember_Id(  Long memberId);

    LocalMember findByLocalMemberName(String go12);
}