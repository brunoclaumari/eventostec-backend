package com.eventostec.api.adapters.outputs.repositories;

import com.eventostec.api.domain.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository <Address, UUID> {
    public Optional<Address> findByEventId(UUID eventId);
}
