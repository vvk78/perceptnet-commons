package com.perceptnet.restclient;

import com.perceptnet.restclient.dto.HttpMethod;

import java.util.Arrays;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class RestRequest {
    private HttpMethod httpMethod;
    private String path;
    private String requestBody;

    private List<String> extraHeaders;

    RestRequest() {
    }

    public RestRequest(HttpMethod httpMethod, String path, String requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestBody = requestBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public RestObjFormat getObjFormat() {
        return RestObjFormat.json;
    }

    @Override
    public String toString() {
        return httpMethod + " " + path;
    }

    public List<String> getExtraHeaders() {
        return extraHeaders;
    }

    public void setExtraHeaders(List<String> extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    public void extraHeaders(String ... extraHeaders) {
        setExtraHeaders(extraHeaders == null ? null : Arrays.asList(extraHeaders));
    }
}
