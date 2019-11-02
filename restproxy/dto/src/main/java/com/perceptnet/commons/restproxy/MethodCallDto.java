package com.perceptnet.commons.restproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 26.06.2018
 */
public class MethodCallDto {
    private String serviceName;
    private String methodName;
    private String methodSignature;

    private List methodArguments = new ArrayList<>(5);

    public MethodCallDto(String serviceName, String methodName, String methodSignature) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public List getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List methodArguments) {
        this.methodArguments = methodArguments;
    }
}
