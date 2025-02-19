package com.eventostec.api.domain.event;

import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEventRepository {

    Event save(Event event);

    Optional<Event> findById(UUID id);

    List<Event> findAll();

    void deleteById(UUID id);

    public Page<EventAddressProjection> findUpcomingEvents(int page, int size);

    Page<EventAddressProjection> findFilteredEvents(String city, String uf, Date startDate, Date endDate, int page, int size);

    List<EventAddressProjection> findEventsByTitle(String title);
}
