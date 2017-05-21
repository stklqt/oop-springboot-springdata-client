package de.andrena.springworkshop.controllers;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.facades.EventFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AgendaController {

    @Autowired
    private EventFacade eventFacade;

    @RequestMapping( value = "/agenda")
    public String agenda(@RequestParam(value = "page", defaultValue = "0") int page,
                         final Model model)
    {
        PagedResources<EventDTO> events = eventFacade.getAllEvents(page);
        model.addAttribute("events", events.getContent());
        model.addAttribute("pagination", events.getMetadata());
        return "mainPage";
    }

    @RequestMapping(value = "/search")
    public String searchByTitle(@RequestParam(value = "title", defaultValue = "") final String title,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                final Model model) {
        PagedResources<EventDTO> events = eventFacade.getEventWithTitle(title, page);
        model.addAttribute("events", events.getContent());
        model.addAttribute("pagination", events.getMetadata());
        model.addAttribute("title", title);
        return "searchPage";
    }

    @RequestMapping(value = "/reactAgenda")
    public String reactAgenda() {
        return "mainPageReact";
    }
}
