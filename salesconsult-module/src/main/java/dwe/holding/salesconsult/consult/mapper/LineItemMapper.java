package dwe.holding.salesconsult.consult.mapper;

import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.Estimatelineitem;
import dwe.holding.salesconsult.sales.model.LineItem;
import dwe.holding.salesconsult.sales.model.Refund;
import dwe.holding.salesconsult.sales.model.RefundLineItem;
import org.mapstruct.*;

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", ignore = true)
    RefundLineItem toRefundLineItem(
            LineItem lineItem,
            @Context Refund refund
    );

    List<RefundLineItem> toRefundLineItemList(
            List<LineItem> lineItem,
            @Context Refund refund
    );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", ignore = true)
    LineItem fromRefundLineItem(
            RefundLineItem refundLineItem,
            @Context Appointment app
    );

    List<LineItem> fromRefundLineItemList(
            List<RefundLineItem> refundLineItem,
            @Context Appointment app
    );



}