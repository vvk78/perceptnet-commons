package com.perceptnet.restclient;

import com.perceptnet.commons.utils.Base64;
import com.perceptnet.restclient.dto.HttpMethod;
import com.perceptnet.restclient.dto.RestMethodDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 29.11.2017
 */
public class BaseRestServiceProvider {
    private Logger log = LoggerFactory.getLogger(getClass());

    private final Object restServiceProxy;
    private RestMethodDescriptionProvider restMethodDescriptionProvider;
    private final RestServiceInvocationHandler handler;
    private final RestCaller restCaller;

    public BaseRestServiceProvider(String baseUrl, RestMethodDescriptionProvider restMethodDescriptionProvider, Class[] restServices) {
        this(baseUrl, restMethodDescriptionProvider, null, restServices);
    }

    public BaseRestServiceProvider(String baseUrl, RestMethodDescriptionProvider restMethodDescriptionProvider,
                                   MessageConverter messageConverter, Class[] restServices) {
        this.restMethodDescriptionProvider = restMethodDescriptionProvider;
        this.restCaller = obtainRestCaller();
        if (messageConverter == null) {
            messageConverter = obtainAnyDefaultMessageConverter();
        }
        this.handler = new RestServiceInvocationHandler(baseUrl, restMethodDescriptionProvider, this.restCaller, messageConverter);
        this.restServiceProxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), restServices, handler);
    }

    public <S> S getRestService(Class<S> serviceClass) {
        return (S) restServiceProxy;
    }

    public RestCaller getRestCaller() {
        return restCaller;
    }

    protected RestCaller obtainRestCaller() {
        return obtainComponent(RestCaller.class, "RestServiceProvider.RestCaller", "rest caller",
                new String[]{"com.perceptnet.restclient.apache.HttpClientRestCallerImpl"});
    }

    protected MessageConverter obtainAnyDefaultMessageConverter() {
        return obtainComponent(MessageConverter.class, "RestServiceProvider.Converter", "rest message converter",
                new String[]{"com.perceptnet.restclient.jackson.JacksonMessageConverter"});
    }

    protected <T> T obtainComponent(Class<T> clazz, String configPropName, String componentDescription, String[] knownDefaultImplementations) {
        T result;

        final String errorMsgBase = "Cannot obtain " + componentDescription + ". Make sure it is configured either via " + configPropName +
                " system property or one of known default implementations\n\t" + join(knownDefaultImplementations, "\n\t")
                + "\nis available on classpath.";

        try {
            String className = System.getProperty(configPropName);
            if (className != null) {
                Class<?> aClass = Class.forName(className);
                return (T) aClass.newInstance();
            }
            result = tryInstantiateFromKnownImplementations(clazz, knownDefaultImplementations);
        } catch (Exception e) {
            throw new RuntimeException(errorMsgBase + " Cause: " + e, e);
        }

        if (result == null) {
            throw new RuntimeException(errorMsgBase);
        }

        return result;
    }

    protected RestServiceInvocationHandler getHandler() {
        return handler;
    }

    public void setRestCallEventsListener(RestCallEventListener eventListener) {
        handler.setEventListener(eventListener);
    }

    protected <T> T tryInstantiateFromKnownImplementations(Class<T> expectedType, String ... classNames) {
        for (String className : classNames) {
            Class<?> callerClass;
            try {
                callerClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.warn("{} implementation not found, skipped", className, e);
                continue;
            }

            try {
                return (T) callerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate " + className + " due to " + e, e);
            }
        }
        return null;
    }

    protected void signInBasic(String singInPath, String login, String password) {
        RestRequestBuilder rb = handler.getRequestBuilder();
        RestMethodDescription md = new RestMethodDescription(HttpMethod.post, new String[]{singInPath}, null);
        RestRequest r = rb.build(handler.getBaseUrl(), md, null);
        r.extraHeaders("Authorization Basic " + b64(login +":" + password));
        handler.doInvokeRest(r);
    }

    protected void signOut(String singOutPath) {
        RestRequestBuilder rb = handler.getRequestBuilder();
        RestMethodDescription md = new RestMethodDescription(HttpMethod.post, new String[]{singOutPath}, null);
        RestRequest r = rb.build(handler.getBaseUrl(), md, null);
        handler.doInvokeRest(r);
    }

    private String b64(String str) {
        try {
            String result = new String(Base64.encode(str.getBytes("UTF-8")), StandardCharsets.UTF_8);
//            String result = new String(Base64.getEncoder().encode(str.getBytes("UTF-8")), StandardCharsets.UTF_8);
//            BASE64Encoder e = new BASE64Encoder();
//            String result = e.encode(str.getBytes("UTF-8"));
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    //protected void signInBasic(String singInPath)

    private String join(String[] items, String with) {
        StringBuilder buff = new StringBuilder(items.length * 100);
        for (String item : items) {
            if (buff.length() > 0) {
                buff.append(with);
            }
            buff.append(item);
        }
        return buff.toString();
    }
}
