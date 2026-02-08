package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.LookupRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookupRoomRepository extends JpaRepository<LookupRoom, Long> {
    List<LookupRoom> getByMemberIdOrderByRoom(Long memberId);

    List<LookupRoom> findByLocalMemberIdAndMemberId(Long Mlid, Long mid);
}