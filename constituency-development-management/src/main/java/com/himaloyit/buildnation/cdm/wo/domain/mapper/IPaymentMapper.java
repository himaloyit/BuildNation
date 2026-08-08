package com.himaloyit.buildnation.cdm.wo.domain.mapper;

import com.himaloyit.buildnation.cdm.wo.domain.dto.PaymentDTO;
import com.himaloyit.buildnation.cdm.wo.domain.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IPaymentMapper {

    @Mapping(target = "workOrderId", source = "workOrder.id")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "workOrder", ignore = true)
    Payment toEntity(PaymentDTO paymentDTO);
}
