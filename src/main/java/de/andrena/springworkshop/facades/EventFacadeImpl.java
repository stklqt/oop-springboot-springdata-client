package de.andrena.springworkshop.facades;

import de.andrena.springworkshop.dao.EventDao;
import de.andrena.springworkshop.dto.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Component;

@Component
public class EventFacadeImpl implements EventFacade {

    @Autowired
    private EventDao eventDao;

    @Override
    public PagedResources<EventDTO> getAllEvents(int page) {
        return eventDao.getAllEvents(page);
    }

    @Override
    public PagedResources<EventDTO> getEventsWithDescriptionContaining(String description, int page) {
        return eventDao.getEventsWithDescriptionContaining(description, page);
    }

    @Override
    public PagedResources<EventDTO> getEventWithTitle(String title, int page) {
        return eventDao.getEventsWithTitleContaining(title, page);
    }
}
