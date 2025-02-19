package com.eventostec.api.application.service;

import com.eventostec.api.adapters.outputs.storage.ImageUploaderPort;
import com.eventostec.api.application.usecases.IEventUseCases;
import com.eventostec.api.domain.address.Address;
import com.eventostec.api.domain.coupon.Coupon;
import com.eventostec.api.domain.event.*;
import com.eventostec.api.utils.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventUseCases {

    @Value("${admin.key}")
    private String adminKey;

    private final AddressService addressService;
    private final CouponService couponService;

    private final IEventRepository repository;
    private final ImageUploaderPort imageUploaderPort;

    @Autowired
    private EventMapper mapper;

    public Event createEvent(EventRequestDTO data) {
        String imgUrl = "";


        if (data.image() != null) {
            imgUrl = imageUploaderPort.uploadImg(data.image());
        }
        Event newEvent = mapper.toEntity(data, imgUrl);
        repository.save(newEvent);

        if (Boolean.FALSE.equals(data.remote())) {
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size) {

        Page<EventAddressProjection> eventsPage = this.repository.findUpcomingEvents(page,size);

        return eventsPage.map(event -> mapper.eventAddressProjectionToEventResponseDto(event)              )
                .stream().toList();

//        return eventsPage.map(event -> new EventResponseDTO(
//                        event.getId(),
//                        event.getTitle(),
//                        event.getDescription(),
//                        event.getDate(),
//                        event.getCity() != null ? event.getCity() : "",
//                        event.getUf() != null ? event.getUf() : "",
//                        event.getRemote(),
//                        event.getEventUrl(),
//                        event.getImgUrl())
//                )
//                .stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Optional<Address> address = addressService.findByEventId(eventId);

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        return mapper.domainToDetailsDto(event, address, coupons);
    }

    public void deleteEvent(UUID eventId, String adminKey){
        if(adminKey == null || !adminKey.equals(this.adminKey)){
            throw new IllegalArgumentException("Invalid admin key");
        }

        this.repository.deleteById(eventId);

//        this.repository.delete(this.repository.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("Event not found")));

    }

    public List<EventResponseDTO> searchEvents(String title){
        title = (title != null) ? title : "";

        List<EventAddressProjection> eventsList = this.repository.findEventsByTitle(title);
        return eventsList.stream()
                .map(event -> mapper.eventAddressProjectionToEventResponseDto(event))
                .toList();

//        return eventsList.stream().map(event -> new EventResponseDTO(
//                        event.getId(),
//                        event.getTitle(),
//                        event.getDescription(),
//                        event.getDate(),
//                        event.getCity() != null ? event.getCity() : "",
//                        event.getUf() != null ? event.getUf() : "",
//                        event.getRemote(),
//                        event.getEventUrl(),
//                        event.getImgUrl())
//                )
//                .toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String uf, Date startDate, Date endDate){
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Page<EventAddressProjection> eventsPage = this.repository.findFilteredEvents(city, uf, startDate, endDate, page, size);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getUf() != null ? event.getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .stream().toList();
    }

//    private String uploadImg(MultipartFile multipartFile) {
//        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
//
//        try {
//            PutObjectRequest putOb = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(filename)
//                    .build();
//            s3Client.putObject(putOb, RequestBody.fromByteBuffer(ByteBuffer.wrap(multipartFile.getBytes())));
//            GetUrlRequest request = GetUrlRequest.builder()
//                    .bucket(bucketName)
//                    .key(filename)
//                    .build();
//            return s3Client.utilities().getUrl(request).toString();
//        } catch (Exception e) {
//            log.error("erro ao subir arquivo: {}", e.getMessage());
//            return "";
//        }
//    }

}
