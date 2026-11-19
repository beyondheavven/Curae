package com.vention.curae.gateway;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class AggregatedApiDocsController {

	private static final Logger log = LoggerFactory.getLogger(AggregatedApiDocsController.class);

	private static final ParameterizedTypeReference<Map<String, Object>> DOC_TYPE = new ParameterizedTypeReference<>() {
	};

	private final GatewayProperties properties;

	private final RestClient restClient;

	public AggregatedApiDocsController(GatewayProperties properties, RestClient.Builder restClientBuilder) {
		this.properties = properties;
		this.restClient = restClientBuilder.build();
	}

	@GetMapping("/v3/api-docs")
	public ResponseEntity<Map<String, Object>> aggregatedDocs() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("openapi", "3.0.1");
		result.put("info", Map.of("title", "Curae API", "version", "1.0"));

		Map<String, Object> paths = new LinkedHashMap<>();
		Map<String, Object> schemas = new LinkedHashMap<>();
		List<Object> tags = new ArrayList<>();

		for (GatewayProperties.Service service : properties.getServices()) {
			try {
				Map<String, Object> doc = restClient.get()
					.uri(service.getUrl() + "/v3/api-docs")
					.retrieve()
					.body(DOC_TYPE);
				if (doc == null) {
					continue;
				}
				mergeObjects(doc.get("paths"), paths);
				mergeObjects(doc.get("components"), schemas, "schemas");
				Object docTags = doc.get("tags");
				if (docTags instanceof List<?> values) {
					tags.addAll(values);
				}
			}
			catch (Exception ex) {
				log.warn("Failed to load OpenAPI document from {}", service.getName(), ex);
			}
		}

		result.put("paths", paths);
		result.put("components", Map.of("schemas", schemas));
		result.put("tags", tags);
		return ResponseEntity.ok(result);
	}

	private void mergeObjects(Object source, Map<String, Object> target) {
		if (source instanceof Map<?, ?> values) {
			values.forEach((key, value) -> target.put(key.toString(), value));
		}
	}

	private void mergeObjects(Object source, Map<String, Object> target, String nestedKey) {
		if (source instanceof Map<?, ?> values && values.get(nestedKey) instanceof Map<?, ?> nested) {
			nested.forEach((key, value) -> target.put(key.toString(), value));
		}
	}

}
