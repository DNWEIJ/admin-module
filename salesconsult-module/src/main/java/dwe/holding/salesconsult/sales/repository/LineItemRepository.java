package dwe.holding.salesconsult.sales.repository;


import dwe.holding.salesconsult.sales.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LineItemRepository extends JpaRepository<LineItem, Long> {

}