package com.perceptnet.restclient.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is to be saved\loaded to from json format. It keeps rest service description for a module (set of services)
 *
 * created by vkorovkin on 18.06.2018
 */
public class ServiceRestRegistryDto {

    /**
     * Methods descriptions mapped by method name + fully qualified signature e.g createUser(com.xyz.UserData,java.lang.Long,java.util.Date)
     */
    private Map<String, RestMethodDescription> methods = new HashMap<>();

    public Map<String, RestMethodDescription> getMethods() {
        return methods;
    }
}
