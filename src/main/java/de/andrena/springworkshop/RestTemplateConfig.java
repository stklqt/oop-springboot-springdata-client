package de.andrena.springworkshop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Traverson traverson() {
		UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.newInstance();
		URI uri = apiUrlBuilder.scheme("http").host("localhost").port(8090).build().toUri();
		Traverson traverson = new Traverson(uri, MediaTypes.HAL_JSON);
		traverson.setRestOperations(restTemplate());
		return traverson;
	}

}
