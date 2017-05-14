package de.andrena.springworkshop.facades;

import de.andrena.springworkshop.dto.EventDTO;

import java.util.List;

public interface EventFacade {
    List<EventDTO> getAllEvents();

    List<EventDTO> getEventsWithDescriptionContaining(String description);

    List<EventDTO> getEventWithTitle(String title);
}
