package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public PagedResources<EventDTO> getAllEvents(int page) {
		PagedResources<Resource<EventDTO>> eventResponse = traverson
				.follow("events")
				.withTemplateParameters(Collections.unmodifiableMap(Stream.of(
						new SimpleEntry<String, Object>("page", page),
						new SimpleEntry<String, Object>("size", 5))
						.collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()))))
				.toObject(new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
				});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private PagedResources<EventDTO> mapToDTOList(PagedResources<Resource<EventDTO>> eventResponse) {
		List<EventDTO> events = eventResponse.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
		return new PagedResources<>(events, eventResponse.getMetadata());
	}

	private void injectSpeakers(PagedResources<Resource<EventDTO>> eventResponse) {
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
	public PagedResources<EventDTO> getEventsWithTitleContaining(String title, int page) {
		PagedResources<Resource<EventDTO>> eventResponse = traverson
				.follow("events", "search", "findByTitleContaining")
				.withTemplateParameters(Collections.unmodifiableMap(Stream.of(
						new SimpleEntry<String, Object>("title", title),
						new SimpleEntry<String, Object>("page", page),
						new SimpleEntry<String, Object>("size", 5))
						.collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()))))
				.toObject(new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	@Override
	public PagedResources<EventDTO> getEventsWithDescriptionContaining(String description, int page) {
		PagedResources<Resource<EventDTO>> eventResponse = traverson
				.follow("events", "search", "findByDescriptionContaining")
				.withTemplateParameters(Collections.unmodifiableMap(Stream.of(
						new SimpleEntry<String, Object>("description", description),
						new SimpleEntry<String, Object>("page", page),
						new SimpleEntry<String, Object>("size", 5))
						.collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()))))
				.toObject(new ParameterizedTypeReference<PagedResources<Resource<EventDTO>>>() {
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
