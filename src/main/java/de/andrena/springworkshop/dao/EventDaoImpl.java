package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventDaoImpl implements EventDao {

	private static final String BASE_URL = "http://localhost:8090";
	private RestTemplate restTemplate;

	@Autowired
	public EventDaoImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}


	@Override
	public List<EventDTO> getAllEvents() {
		Link eventsURL = getLinkForRef("events", BASE_URL);
		if (eventsURL != null) {
			Resources<Resource<EventDTO>> eventResponse = sendRequest(eventsURL.expand(Collections.emptyMap()).getHref(), new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
			});
			injectSpeakers(eventResponse);
			return mapToDTOList(eventResponse);
		}
		return null;
	}

	private List<EventDTO> mapToDTOList(Resources<Resource<EventDTO>> eventResponse) {
		return eventResponse.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
	}

	private void injectSpeakers(Resources<Resource<EventDTO>> eventResponse) {
		if (eventResponse != null) {
			ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>> speakerType = new ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>>() {
			};
			for (Resource<EventDTO> event : eventResponse) {
				if (event.hasLink("speakers")) {
					Resources<Resource<SpeakerDTO>> speakerResources = sendRequest(event.getLink("speakers").expand(Collections.emptyMap()).getHref(), speakerType);
					if (speakerResources != null) {
						List<SpeakerDTO> speakerDTOList = speakerResources.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
						event.getContent().setSpeakers(speakerDTOList);
					}
				}
			}
		}
	}

	@Override
	public List<EventDTO> getEventsWithTitleContaining(String title) {
		Link eventsLink = getLinkForRef("events", BASE_URL);
		Link searchLink = getLinkForRef("search", eventsLink.expand(Collections.emptyMap()).getHref());
		Link findByTitleContainingLink = getLinkForRef("findByTitleContaining", searchLink.expand(Collections.emptyMap()).getHref());
		Resources<Resource<EventDTO>> eventResponse = sendRequest(findByTitleContainingLink.expand(Collections.singletonMap("title", title)).getHref(), new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	@Override
	public List<EventDTO> getEventsWithDescriptionContaining(String description) {
		Link eventsLink = getLinkForRef("events", BASE_URL);
		Link searchLink = getLinkForRef("search", eventsLink.expand(Collections.emptyMap()).getHref());
		Link findByDescriptionContainingLink = getLinkForRef("findByDescriptionContaining", searchLink.expand(Collections.emptyMap()).getHref());

		Resources<Resource<EventDTO>> eventResponse = sendRequest(findByDescriptionContainingLink.expand(Collections.singletonMap("description", description)).getHref(), new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private Link getLinkForRef(String rel, String url) {
		ResponseEntity<Resource<Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Object>>() {
		});
		if (response != null && response.hasBody()) {
			Resource<Object> body = response.getBody();
			return body.getLink(rel);
		}
		return null;
	}

	private <T> T sendRequest(String url, ParameterizedTypeReference<T> type) {
		ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, type);
		if (response != null) {
			return response.getBody();
		}
		return null;
	}

}
