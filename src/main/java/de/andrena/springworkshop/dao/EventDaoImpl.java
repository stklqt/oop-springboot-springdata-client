package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventDaoImpl implements EventDao {

    private final RestTemplate restTemplate;

    private String scheme = "http";
    private String host = "localhost:8090";

    @Autowired
    public EventDaoImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public List<EventDTO> getAllEvents() {
        final String url = getUrl("events");
        Resources<Resource<EventDTO>> eventResponse = sendRequest(url, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
        });

        linkSpeaker(eventResponse);
        return mapToDTOList(eventResponse);
    }

    private List<EventDTO> mapToDTOList(Resources<Resource<EventDTO>> eventResponse) {
        return eventResponse.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
    }

    private <T> T sendRequest(String url, ParameterizedTypeReference<T> type) {
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, type);
        if (response != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    public List<EventDTO> getEventsWithTitleContaining(String title) {
        final String searchPath = "/search/findByTitleContaining";
        final String url = getUrl("events") + searchPath;
        final UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.fromHttpUrl(url);
        final String requestUrl = apiUrlBuilder.queryParam("title", title).build().toString();

        Resources<Resource<EventDTO>> eventResponse = sendRequest(requestUrl, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
        });

        linkSpeaker(eventResponse);
        return mapToDTOList(eventResponse);
    }

    @Override
    public List<EventDTO> getEventsWithDescriptionContaining(String description) {
        //not yet implemented :(
        return null;
    }


    /*  HELPER METHODS */
    private String getUrl(String link) {
        final String path = "/";
        final UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.newInstance();

        String url = apiUrlBuilder.scheme(scheme).host(host).path(path).build().toString();

        Resources<Resource<EventDTO>> eventResponse = sendRequest(url, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
        });
        return eventResponse.getLink(link).getHref();
    }


    private void linkSpeaker(Resources<Resource<EventDTO>> eventResponse) {
        ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>> speakerType
                = new ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>>() {
        };

        for (Resource<EventDTO> event : eventResponse) {
            String speakersUrl = event.getLink("speakers").getHref();
            Resources<Resource<SpeakerDTO>> speakerResources = sendRequest(speakersUrl, speakerType);
            if (speakerResources != null) {
                List<SpeakerDTO> speakerDTOList = speakerResources.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
                event.getContent().setSpeakers(speakerDTOList);
            }
        }
    }

}
