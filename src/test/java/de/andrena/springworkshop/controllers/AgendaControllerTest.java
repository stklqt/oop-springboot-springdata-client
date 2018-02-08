package de.andrena.springworkshop.controllers;

import de.andrena.springworkshop.dao.EventDao;
import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.facades.EventFacade;
import de.andrena.springworkshop.facades.EventFacadeImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest()
@RunWith(SpringRunner.class)
public class AgendaControllerTest {

	private static final String SEARCH_TERM = "myTestTitle";
	// @MockBean is a Spring Boot annotation - adds mocks to a Spring ApplicationContext
	// Different to @Mock from Mockito, which doesn't have anything to do with Spring Boot
	@MockBean
	private EventDao eventDao;
	// MockMVC mocks all the Spring MVC infrastructure
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void agenda_modelContainsEvents() throws Exception {
		List<EventDTO> eventList = Collections.singletonList(new EventDTO());
		when(eventDao.getAllEvents()).thenReturn(eventList);

		mockMvc.perform(get("/agenda"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("events", eventList));
	}

	@Test
	public void search_modelContainsCorrectEvents() throws Exception {
		//Extra Übung: Test implementieren!
	}

	// @TestConfiguration is used to customise the Spring test context here
	// Modifies the current context
	// Spring caches the test contexts between tests -> no need to reload context (saves time)
	// as long as the tests share the same context
	@TestConfiguration
	static class EventFacadeTestConfiguration {

		@Bean
		public EventFacade eventFacade() {
			return new EventFacadeImpl();
		}
	}
}
