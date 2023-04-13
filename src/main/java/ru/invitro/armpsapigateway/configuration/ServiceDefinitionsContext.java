package ru.invitro.armpsapigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceDefinitionsContext {

    @Bean("mapper")
    public ObjectMapper buildObjectMapper() {
        return Json.mapper();
    }

    @Autowired
    private GatewayProperties gatewayProperties;

    private static final ConcurrentHashMap<String, String> serviceDescriptions = new ConcurrentHashMap();

    public static ConcurrentHashMap<String, String> getServiceDescriptions() {
        return serviceDescriptions;
    }
    private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionUpdater.class);

    @Bean("mapWrapper")
    public MapWrapper getMapWrapper() {
        refreshSwaggerConfigurations();
        MapWrapper mapWrapper = new MapWrapper();
        mapWrapper.setServiceDescriptions(serviceDescriptions);
        return mapWrapper;
    }

    public void refreshSwaggerConfigurations() {
        logger.debug("Starting Service Definition Context refresh");
        gatewayProperties.getRoutes().stream().forEach(route -> {
            logger.debug("Attempting service definition refresh for Service : {} ", route.getId());
            String swaggerURL = route.getUri() + (String) route.getMetadata().get("swagger-path");
            logger.error("!==============================================Swagger URL : {} ", swaggerURL);
            Optional<Object> jsonData = getSwaggerDefinitionForAPI(route.getId(), swaggerURL);

            if (jsonData.isPresent()) {
                String content = getJSON(jsonData.get());
                serviceDescriptions.put(route.getId(), content);
            } else {
                logger.error("Skipping service id : {} Error : Could not get Swager definition from API ", route.getId());
            }

            logger.info("Service Definition Context Refreshed at :  {}", LocalDate.now());

        });
    }

    private Optional <Object> getSwaggerDefinitionForAPI(String serviceId, String url) {
        logger.debug("Accessing the SwaggerDefinition JSON for URL : {}, serviceId : {}", url, serviceId);
        try {
            RestTemplate restTemplate = new RestTemplate();
            Object jsonData = restTemplate.getForObject(url, Object.class);
            return Optional.of(jsonData);
        } catch (RestClientException ex) {
            logger.error("Error while getting service definition: {}, Error : {} ", serviceId, ex.getMessage());
            return Optional.empty();
        }

    }

    public String getJSON(Object jsonData) {
        try {
            return new ObjectMapper().writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            logger.error("Error : {} ", e.getMessage());
            return "";
        }
    }

    @Getter
    @Setter
    public static class MapWrapper {
        ConcurrentHashMap<String, String> serviceDescriptions = new ConcurrentHashMap();
    }
}
