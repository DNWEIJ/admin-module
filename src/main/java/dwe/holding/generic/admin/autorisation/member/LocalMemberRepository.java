package dwe.holding.generic.admin.autorisation.member;

import dwe.holding.generic.admin.model.LocalMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalMemberRepository extends JpaRepository<LocalMember, Long> {

}