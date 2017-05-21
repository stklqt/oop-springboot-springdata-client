package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import org.springframework.hateoas.PagedResources;

public interface EventDao {

    PagedResources<EventDTO> getAllEvents(int page);

    PagedResources<EventDTO> getEventsWithTitleContaining(String title, int page);

    PagedResources<EventDTO> getEventsWithDescriptionContaining(String description, int page);
}