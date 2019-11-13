package com.perceptnet.restclient.dto;


/**
 * Rest method description (inside service). Suitable for json representation.
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class RestMethodDescription {
    public static final int ALL_ARG_LIST_AS_BODY = -2;

    /**
     * Https method to be executed
     */
    private HttpMethod httpMethod;
    /**
     * Array of path pieces. Argument placeholders are nulls.
     */
    private String[] pathPieces;
    /**
     * Mapping of placeholder index..
     *
     * (which is index of null items in {@link #pathPieces} array
     * E.g. if pathPieces is ["counter", null, "?userid=", null] {@link #pathArgumentIndices} is to be expected to have length 2
     * -- according to number of nulls in {@link #pathPieces} array)
     *
     * .. to index of java method argument. (Index of java method argument corresponds to index in this array).
     */
    private int[] pathArgumentIndices;
    /**
     * If an argument is to be passed as request body, this field keeps its index among method arguments and is
     * -1 if there is no body argument at all.
     * -2 ({@link #ALL_ARG_LIST_AS_BODY}) if the whole list of method arguments is to be passed as request body
     */
    private int bodyArgumentIndex;

    public RestMethodDescription() {
    }

    public RestMethodDescription(HttpMethod httpMethod, String[] pathPieces, int[] pathArgumentIndices) {
        this(httpMethod, pathPieces, pathArgumentIndices, -1);
    }

    public RestMethodDescription(HttpMethod httpMethod, String[] pathPieces, int[] pathArgumentIndices, int bodyArgumentIndex) {
        this.httpMethod = httpMethod;
        this.pathPieces = pathPieces;
        this.pathArgumentIndices = pathArgumentIndices;
        this.bodyArgumentIndex = bodyArgumentIndex;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String[] getPathPieces() {
        return pathPieces;
    }

    public int[] getPathArgumentIndices() {
        return pathArgumentIndices;
    }

    public int getBodyArgumentIndex() {
        return bodyArgumentIndex;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setPathPieces(String[] pathPieces) {
        this.pathPieces = pathPieces;
    }

    public void setPathArgumentIndices(int[] pathArgumentIndices) {
        this.pathArgumentIndices = pathArgumentIndices;
    }

    public void setBodyArgumentIndex(int bodyArgumentIndex) {
        this.bodyArgumentIndex = bodyArgumentIndex;
    }
}
