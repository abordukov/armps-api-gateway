/*
package ru.invitro.armpsapigateway.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("ServiceController")
@ComponentScan
@Tag(name = "Service")
@Lazy
public class ServiceDefinitionController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Найден",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/service/{servicename}")
    public String getServiceDefinition(@PathVariable("servicename") String serviceName) {
        return null;
    }
}
*/
