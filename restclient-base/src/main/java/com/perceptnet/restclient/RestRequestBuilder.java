package com.perceptnet.restclient;

import com.perceptnet.restclient.dto.RestMethodDescription;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 27.11.2017
 */
class RestRequestBuilder {
    private MessageConverter converter;

    RestRequestBuilder(MessageConverter converter) {
        this.converter = converter;
    }

    RestRequest build(String baseUrl, RestMethodDescription d, Object[] arguments) {
        RestRequest result = new RestRequest();
        result.setHttpMethod(d.getHttpMethod());
        StringBuilder buff = new StringBuilder(200);
        int argIndex = 0;
        for (String pathPiece : d.getPathPieces()) {
            if (pathPiece != null) {
                //add base url and glue it with next path piece:
                addPathPiece(buff, baseUrl, pathPiece);
            } else {
                int i = d.getPathArgumentIndices()[argIndex++];
                String item = translateArgument(arguments[i]);
                //add base url and glue it with next path piece:
                addPathPiece(buff, baseUrl, item);
            }
        }
        result.setPath(buff.toString());
        if (d.getBodyArgumentIndex() >= 0) {
            String body = converter.format(arguments[d.getBodyArgumentIndex()]);
            //result.setRequestBody(translateArgument(arguments[d.getBodyArgumentIndex()]));
            result.setRequestBody(body);
        }
        return result;
    }

    void addPathPiece(StringBuilder buff, String firstItem, String secondItem) {
        //add base url and glue it with next path piece:
        if (buff.length() == 0) {
            if (firstItem.endsWith("/") && secondItem.startsWith("/")) {
                buff.append(firstItem.substring(0, firstItem.length()-1));
            } else if (!firstItem.endsWith("/") && !secondItem.startsWith("/")) {
                buff.append(firstItem);
                buff.append("/");
            } else {
                buff.append(firstItem);
            }
        }
        buff.append(secondItem);
    }

    String translateArgument(Object argument) {
        if (argument == null) {
            return "";
        }
        try {
            if (argument instanceof String) {
                return URLEncoder.encode((String) argument, "UTF-8");
            }
            return "" + argument;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
