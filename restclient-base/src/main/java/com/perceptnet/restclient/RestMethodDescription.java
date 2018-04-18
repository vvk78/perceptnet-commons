package com.perceptnet.restclient;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
public class RestMethodDescription {
    /**
     * Https method to be executed
     */
    private final HttpMethod httpMethod;
    /**
     * Array of path pieces. Argument placeholders are nulls.
     */
    private final String[] pathPieces;
    /**
     * Mapping of placeholder index (which is order number of null items in pathPieces array starting from 0
     * E.g. if pathPieces is ["counter", null, "?userid=", null] pathArgumentIndices is to be expected to have length 2)
     * to index of method argument
     * Index of method argument corresponds to index in this array.
     */
    private final int[] pathArgumentIndices;
    /**
     * If an argument is to be passed as request body, this field keeps its index among method arguments and is -1 otherwise.
     */
    private final int bodyArgumentIndex;

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
}