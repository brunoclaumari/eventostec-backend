package com.eventostec.api.adapters.outputs.repositories.impl;

import com.eventostec.api.adapters.outputs.entities.JpaEventEntity;
import com.eventostec.api.adapters.outputs.repositories.JpaEventRepository;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventAddressProjection;
import com.eventostec.api.domain.event.IEventRepository;
import com.eventostec.api.utils.mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class EventRepositoryImpl implements IEventRepository {


    private final JpaEventRepository jpaEventRepository;

    @Autowired
    private final EventMapper eventMapper;

    public EventRepositoryImpl(JpaEventRepository jpaEventRepository, EventMapper eventMapper) {
        this.jpaEventRepository = jpaEventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event save(Event event) {
        JpaEventEntity jpaEventEntity = eventMapper.eventToJpaEventEntity(event);
        JpaEventEntity savedEvent =  this.jpaEventRepository.save(jpaEventEntity);
        return eventMapper.jpaEventEntityToEvent(savedEvent);
    }

    @Override
    public Optional<Event> findById(UUID id) {
        Optional<JpaEventEntity> searchedJpaEventEntity = jpaEventRepository.findById(id);
        return searchedJpaEventEntity.map(e -> eventMapper.jpaEventEntityToEvent(e));
    }

    @Override
    public List<Event> findAll() {
        return jpaEventRepository.findAll()
                .stream().map( jpaEntity -> eventMapper.jpaEventEntityToEvent(jpaEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaEventRepository.deleteById(id);
    }

    @Override
    public Page<EventAddressProjection> findUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return this.jpaEventRepository.findUpcomingEvents(new Date(),pageable);
    }

    @Override
    public Page<EventAddressProjection> findFilteredEvents(String city, String uf, Date startDate, Date endDate, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return this.jpaEventRepository.findFilteredEvents(city,uf,startDate,endDate,pageable);
    }

    @Override
    public List<EventAddressProjection> findEventsByTitle(String title) {
        return this.jpaEventRepository.findEventsByTitle(title);
    }
}
