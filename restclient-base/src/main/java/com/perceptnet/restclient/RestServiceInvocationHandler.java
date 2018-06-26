package com.perceptnet.restclient;

import com.perceptnet.restclient.dto.RestMethodDescription;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class RestServiceInvocationHandler extends RestCallerInvocationEventWrapper implements InvocationHandler {
    private String baseUrl;
    private RestMethodDescriptionProvider rmdProvider;
    private RestRequestBuilder requestBuilder;
    private RestCaller restCaller;
    private RestCallEventListener eventListener;

    private MessageConverter converter;

    public RestServiceInvocationHandler(String baseUrl, RestMethodDescriptionProvider rmdProvider, RestCaller restCaller, MessageConverter converter) {
        super(restCaller);
        this.baseUrl = baseUrl;
        this.rmdProvider = rmdProvider;
        this.converter = converter;
        this.requestBuilder = new RestRequestBuilder(this.converter);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RestMethodDescriptionProvider rmdProvider;
        if (proxy instanceof RestMethodDescriptionProvider) {
            rmdProvider = (RestMethodDescriptionProvider) proxy;
        } else {
            rmdProvider = this.rmdProvider;
        }
        if (rmdProvider == null) {
            throw new IllegalStateException("RestMethodDescriptionProvider is not set and not available via proxy");
        }
        RestMethodDescription d = rmdProvider.getMethodDescription(method);
        if (d == null) {
            throw new IllegalStateException("RestMethodDescription is unavailable");
        }
        RestRequest request = requestBuilder.build(baseUrl, d, args);

        Type[] params = method.getGenericParameterTypes();
        if (method.getReturnType() == byte[].class) {
            return doInvokeRestForBytes(request);
        } else {
            String rawResponse = doInvokeRest(request);
            if (method.getReturnType() == void.class) {
                return null;
            } else if (rawResponse == null || rawResponse.isEmpty()) {
                return null;
            } else {
                return converter.parse(method.getReturnType(), rawResponse);
            }
        }

    }

    RestRequestBuilder getRequestBuilder() {
        return requestBuilder;
    }

    String getBaseUrl() {
        return baseUrl;
    }

    public void setRmdProvider(RestMethodDescriptionProvider rmdProvider) {
        this.rmdProvider = rmdProvider;
    }

    public void setRequestBuilder(RestRequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public void setRestCaller(RestCaller restCaller) {
        this.restCaller = restCaller;
    }

    public void setEventListener(RestCallEventListener eventListener) {
        this.eventListener = eventListener;
    }
}
