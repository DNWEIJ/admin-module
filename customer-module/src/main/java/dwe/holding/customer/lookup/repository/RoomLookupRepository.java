package dwe.holding.customer.lookup.repository;

import dwe.holding.customer.client.model.lookup.LookupRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomLookupRepository extends JpaRepository<LookupRoom, Long> {
    List<LookupRoom> getByMemberIdOrderByRoom(Long memberId);
}