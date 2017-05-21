package de.andrena.springworkshop.facades;

import de.andrena.springworkshop.dto.EventDTO;
import org.springframework.hateoas.PagedResources;

public interface EventFacade {
    PagedResources<EventDTO> getAllEvents(int page);

    PagedResources<EventDTO> getEventsWithDescriptionContaining(String description, int page);

    PagedResources<EventDTO> getEventWithTitle(String title, int page);
}
