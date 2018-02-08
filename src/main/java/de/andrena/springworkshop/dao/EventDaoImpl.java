package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public PagedResources<EventDTO> getAllEvents(int page) {
		Link eventsURL = getLinkForRef("events", BASE_URL);
		if (eventsURL != null) {
			PagedResources<Resource<EventDTO>> eventResponse = sendRequest(eventsURL.expand(createParameters(page)).getHref(), new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
			});
			injectSpeakers(eventResponse);
			return mapToDTOList(eventResponse);
		}
		return null;
	}

	private Map<String, String> createParameters(int page) {
		Map<String, String> parameter = new HashMap<>();
		parameter.put("size", "5");
		parameter.put("page", String.valueOf(page));
		return parameter;
	}

	private PagedResources<EventDTO> mapToDTOList(PagedResources<Resource<EventDTO>> eventResponse) {
		List<EventDTO> events = eventResponse.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
		return new PagedResources<>(events, eventResponse.getMetadata());
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
	public PagedResources<EventDTO> getEventsWithTitleContaining(String title, int page) {
		Link eventsLink = getLinkForRef("events", BASE_URL);
		Link searchLink = getLinkForRef("search", eventsLink.expand(Collections.emptyMap()).getHref());
		Link findByTitleContainingLink = getLinkForRef("findByTitleContaining", searchLink.expand(Collections.emptyMap()).getHref());

		PagedResources<Resource<EventDTO>> eventResponse = sendRequest(findByTitleContainingLink.expand(createParametersWithTitle(title, page)).getHref(), new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private Map<String, String> createParametersWithTitle(String title, int page) {
		Map<String, String> parameters = createParameters(page);
		parameters.put("title", title);
		return parameters;
	}

	@Override
	public PagedResources<EventDTO> getEventsWithDescriptionContaining(String description, int page) {
		Link eventsLink = getLinkForRef("events", BASE_URL);
		Link searchLink = getLinkForRef("search", eventsLink.expand(Collections.emptyMap()).getHref());
		Link findByDescriptionContainingLink = getLinkForRef("findByDescriptionContaining", searchLink.expand(Collections.emptyMap()).getHref());

		PagedResources<Resource<EventDTO>> eventResponse = sendRequest(findByDescriptionContainingLink.expand(createParametersWithDescription(description, page)).getHref(), new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private Map<String, String> createParametersWithDescription(String description, int page) {
		Map<String, String> parameters = createParameters(page);
		parameters.put("description", description);
		return parameters;
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
