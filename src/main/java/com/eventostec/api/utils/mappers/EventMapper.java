package com.eventostec.api.utils.mappers;

import com.eventostec.api.adapters.outputs.entities.JpaEventEntity;
import com.eventostec.api.domain.address.Address;
import com.eventostec.api.domain.coupon.Coupon;
import com.eventostec.api.domain.event.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

        @Mappings({
                @Mapping(target = "id", ignore = true),
                @Mapping(source = "dto.title", target = "title"),
                @Mapping(source = "dto.description", target = "description"),
                @Mapping(target = "imgUrl", source = "imgUrl"),
                @Mapping(source = "dto.eventUrl", target = "eventUrl"),
                @Mapping(source = "dto.date", target = "date", qualifiedByName = "epochToDate"),
                @Mapping(source = "dto.remote", target = "remote"),
        })
        Event toEntity(EventRequestDTO dto, String imgUrl);

        @Mappings({
                @Mapping(source = "entity.title", target = "title"),
                @Mapping(source = "entity.description", target = "description"),
                @Mapping(source = "entity.eventUrl", target = "eventUrl"),
                @Mapping(source = "entity.date", target = "date", qualifiedByName = "dateToEpoch"),
                @Mapping(source = "entity.remote", target = "remote"),
        })
        EventRequestDTO toDto(Event entity);


    @Named("epochToDate")
    default Date epochToDate(Long timestamp) {
        return timestamp != null ? new Date(timestamp) : null;
    }

    @Named("dateToEpoch")
    default Long dateToEpoch(Date date) {
        return date != null ? date.getTime() : null;
    }

    JpaEventEntity eventToJpaEventEntity(Event event);


    Event jpaEventEntityToEvent(JpaEventEntity jpaEventEntity);

    @Mapping(source = "eventAddressProjection.city", target = "city", defaultValue = "")
    @Mapping(source = "eventAddressProjection.uf", target = "uf", defaultValue = "")
    EventResponseDTO eventAddressProjectionToEventResponseDto(EventAddressProjection eventAddressProjection);

    default EventDetailsDTO domainToDetailsDto(Event event, Optional<Address> address, List<Coupon> coupons){

        String city = address.isPresent() ? address.get().getCity() : "";
        String uf = address.isPresent() ? address.get().getUf() : "";

        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                city,
                uf,
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs);
    }

}
