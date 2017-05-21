package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventDaoImpl implements EventDao {

	private final Traverson traverson;
	private RestTemplate restTemplate;

	@Autowired
	public EventDaoImpl(Traverson traverson, RestTemplate restTemplate) {
		this.traverson = traverson;
		this.restTemplate = restTemplate;
	}


	@Override
	public List<EventDTO> getAllEvents() {
		Resources<Resource<EventDTO>> eventResponse = traverson.follow("events").toObject(new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
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
					Resources<Resource<SpeakerDTO>> speakerResources = sendRequest(event.getLink("speakers").getHref(), speakerType);
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
		Resources<Resource<EventDTO>> eventResponse = traverson.follow("events", "search", "findByTitleContaining").withTemplateParameters(Collections.singletonMap("title", title)).toObject(new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	@Override
	public List<EventDTO> getEventsWithDescriptionContaining(String description) {
		Resources<Resource<EventDTO>> eventResponse = traverson.follow("events", "search", "findByDescriptionContaining").withTemplateParameters(Collections.singletonMap("description", description)).toObject(new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private <T> T sendRequest(String url, ParameterizedTypeReference<T> type) {
		ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, type);
		if (response != null) {
			return response.getBody();
		}
		return null;
	}

}
