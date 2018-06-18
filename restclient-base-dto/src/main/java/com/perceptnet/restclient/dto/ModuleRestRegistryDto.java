package com.perceptnet.restclient.dto;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class is to be saved\loaded to from json format. It keeps rest service description for a module (set of services)
 *
 * created by vkorovkin on 15.06.2018
 */
public class ModuleRestRegistryDto {
    private Map<String, ServiceRestRegistryDto> services = new HashMap<>();

    public Map<String, ServiceRestRegistryDto> getServices() {
        return services;
    }
}
