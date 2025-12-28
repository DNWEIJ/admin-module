package dwe.holding.salesconsult.consult.mapper;

import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.Estimatelineitem;
import dwe.holding.salesconsult.sales.model.LineItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LineItemMapper {

    Estimatelineitem toEstimateLineItem(
            LineItem lineItem,
            @Context Estimate estimate
    );

    List<Estimatelineitem> toEstimateLineItemList(
            List<LineItem> lineItems,
            @Context Estimate estimate
    );

    @AfterMapping
    default void setEstimate(
            @MappingTarget Estimatelineitem target,
            @Context Estimate estimate
    ) {
        target.setEstimate(estimate);
    }
}