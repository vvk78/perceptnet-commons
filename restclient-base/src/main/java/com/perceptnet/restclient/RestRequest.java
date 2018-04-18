package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class RestRequest {
    private HttpMethod httpMethod;
    private String path;
    private String requestBody;

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
}
