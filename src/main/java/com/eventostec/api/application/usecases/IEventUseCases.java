package com.eventostec.api.application.usecases;

import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventDetailsDTO;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.domain.event.EventResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface IEventUseCases {

    public Event createEvent(EventRequestDTO data);

    public List<EventResponseDTO> getUpcomingEvents(int page, int size);

    public EventDetailsDTO getEventDetails(UUID eventId);

    public void deleteEvent(UUID eventId, String adminKey);

    public List<EventResponseDTO> searchEvents(String title);

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String uf, Date startDate, Date endDate);
}
