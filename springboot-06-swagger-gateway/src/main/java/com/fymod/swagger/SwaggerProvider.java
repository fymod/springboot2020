package com.fymod.swagger;

import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Primary
public class SwaggerProvider implements SwaggerResourcesProvider {
    public static final String API_URI = "/v2/api-docs";
    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;
    private final String [] igores = {"fileuploadapi","files"};

    public SwaggerProvider(RouteLocator routeLocator, GatewayProperties gatewayProperties) {
    	this.routeLocator = routeLocator;
    	this.gatewayProperties = gatewayProperties;
    }
    
    @Override
    public List<SwaggerResource> get() {
    	
    	List<String> igoress = Arrays.asList(igores);
    	
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        routeLocator.getRoutes().subscribe(route -> {
        	if(!igoress.contains(route.getId())&&!(route.getId().startsWith("doc"))) {
        		routes.add(route.getId());
        	}
        });
        
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                        .forEach(predicateDefinition -> resources.add(swaggerResource(routeDefinition,routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                        .replace("/**", API_URI)))));
        return resources;
    }
    
    private SwaggerResource swaggerResource(RouteDefinition routeDefinition,String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation("/"+name+API_URI);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
    
    
}
