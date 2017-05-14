package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;

import java.util.List;

public interface EventDao {

    List<EventDTO> getAllEvents();

    List<EventDTO> getEventsWithTitleContaining(String title);

    List<EventDTO> getEventsWithDescriptionContaining(String description);
}