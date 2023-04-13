package ru.invitro.armpsapigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Configuration
@EnableWebFlux
@ComponentScan(basePackages = {"ru.invitro.armpsapigateway.controller"})
@OpenAPIDefinition
public class OpenAPIConfig implements WebFluxConfigurer {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    List<GroupedOpenApi> groupsToCount = new ArrayList<>();

    @Autowired
    private RouteDefinitionLocator locator;

    @Autowired
    private ServiceDefinitionsContext context;

    @Bean
    public RestTemplate configureTemplate() {
        return new RestTemplate();
    }

    @Autowired
    ServiceDefinitionsContext.MapWrapper mapWrapper;

    public GroupedOpenApi posGroupedOpenApi() {
        GroupedOpenApi gro = GroupedOpenApi.builder()
                .group("gateway")
                .packagesToScan("ru.invitro.armpsapigateway.controller")
                .build();
        return gro;
    }

    @Bean
    @DependsOn("mapWrapper")
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        definitions.forEach(routeDefinition -> {

            String name = routeDefinition.getId().replaceAll("_route", "");
            ObjectMapper objectMapper = Json.mapper();
            String apiString = mapWrapper.getServiceDescriptions().get(name);
            OpenAPI openAPI;

            try {
                 openAPI = objectMapper.readValue(apiString, OpenAPI.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            GroupedOpenApi api = GroupedOpenApi.builder().pathsToMatch("/" + name + "/**")
                    /*.addOpenApiCustomizer(customizer -> {
                        customizer.setInfo(openAPI.getInfo());
                        customizer.setComponents(openAPI.getComponents());
                        customizer.setServers(openAPI.getServers());
                        customizer.setSecurity(openAPI.getSecurity());
                        customizer
                    })
                    .addOperationCustomizer()
                    .addRouterOperationCustomizer()*/
                    .group(name)
                    .build();
            groups.add(api);
        });

        groups.sort(Comparator.comparing(GroupedOpenApi::getGroup));
        groupsToCount.addAll(groups);
        return groups;
    }


    //@Bean
    public OpenAPI getAllApis() {
        List<OpenAPI> apis = new ArrayList();
        ObjectMapper objectMapper = Json.mapper();
        context.getServiceDescriptions().forEach((k, v) -> {
            try {
                OpenAPI openAPI = objectMapper.readValue(v, OpenAPI.class);
                openAPI.setServers(Collections.singletonList(new Server().url("http://localhost:8095/")));
                openAPI.setInfo(new Info().title(k));
                apis.add(openAPI);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        if (atomicInteger.get() >= apis().size()) {
            return new OpenAPI()
                    .components(new Components())
                    .info(new Info().title("Test")
                            .description("Test Description")
                            .version("1.0.0"));
        }
        apis.sort(Comparator.comparing(o -> o.getInfo().getTitle()));
        return apis.get(atomicInteger.getAndIncrement());
    }

}




