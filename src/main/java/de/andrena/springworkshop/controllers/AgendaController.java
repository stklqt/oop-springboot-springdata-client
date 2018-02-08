package de.andrena.springworkshop.controllers;

import de.andrena.springworkshop.facades.EventFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AgendaController {

    @Autowired
    private EventFacade eventFacade;

    @RequestMapping( value = "/agenda")
    public String agenda(final Model model)
    {
        model.addAttribute("events", eventFacade.getAllEvents());
        return "mainPage";
    }

    @RequestMapping(value = "/search")
    public String searchByTitle(@RequestParam(value = "title", defaultValue = "") final String title, final Model model) {
        model.addAttribute("events", eventFacade.getEventWithTitle(title));
        return "searchPage";
    }

}
