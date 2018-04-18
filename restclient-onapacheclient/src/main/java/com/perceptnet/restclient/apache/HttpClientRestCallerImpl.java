package com.perceptnet.restclient.apache;

import com.perceptnet.restclient.RestCaller;
import com.perceptnet.restclient.RestInvocationException;
import com.perceptnet.restclient.RestObjFormat;
import com.perceptnet.restclient.RestRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.11.2017
 */
public class HttpClientRestCallerImpl implements RestCaller {
    private HttpClient httpClient;

    public HttpClientRestCallerImpl() {
        this(null);
    }

    public HttpClientRestCallerImpl(HttpClient httpClient) {
        if (httpClient == null) {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            //    	HttpConnectionParams.setSoTimeout(params, 5);
            httpClient = new DefaultHttpClient(params);
        }
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String invokeRest(RestRequest request) {
        HttpRequestBase httpMethod = createHttpMethod(request);
        ResponseHandlerImpl responseHandler = new ResponseHandlerImpl();
        String responseBody;
        try {
            responseBody = httpClient.execute(httpMethod, responseHandler);
        } catch (ClientProtocolException e) {
            throw new RestInvocationException("Cannot invoke REST " + request + " due to " + e, e);
        } catch (IOException e) {
            throw new RestInvocationException("Cannot invoke REST " + request + " due to " + e, e);
        }
        if (responseHandler.response != null) {
            int statusCode = responseHandler.response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new RestInvocationException(statusCode, responseBody);
            }
        }

        return responseBody;
    }

    @Override
    public byte[] invokeRestForBytes(RestRequest request) {
        HttpRequestBase httpMethod = createHttpMethod(request);
        String responseBody;
        try {
            HttpResponse response = httpClient.execute(httpMethod);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (statusCode < 200 || statusCode >= 300) {
                throw new RestInvocationException(statusCode, EntityUtils.toString(entity));
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            entity.writeTo(bos);
            return bos.toByteArray();
        } catch (ClientProtocolException e) {
            throw new RestInvocationException("Cannot invoke REST " + request + " due to " + e, e);
        } catch (IOException e) {
            throw new RestInvocationException("Cannot invoke REST " + request + " due to " + e, e);
        }
    }

    private HttpRequestBase createHttpMethod(RestRequest request) {
        HttpRequestBase result = null;
        HttpEntityEnclosingRequestBase envelop = null;
        switch (request.getHttpMethod()) {
            case get:
                result = new HttpGet(request.getPath());
                break;

            case post:
                envelop = new HttpPost(request.getPath());
                break;
            case put:
                envelop = new HttpPut(request.getPath());
                break;
            case patch:
                //envelop = new HttpPatch(request.getPath());
                throw new UnsupportedOperationException("Path HTTP method is not supported with this version");

            case delete:
                result = new HttpDelete(request.getPath());
                break;

            case head:
                result = new HttpHead(request.getPath());
                break;

            case trace:
                result = new HttpTrace(request.getPath());
                break;

            case options:
                result = new HttpTrace(request.getPath());
                break;

            default:
                throw new UnsupportedOperationException("Unsupported http result: " + request.getHttpMethod());
        }

        if (envelop != null) {
            if (request.getRequestBody() != null) {
                if (request.getObjFormat() == RestObjFormat.json) {
                    try {
                        envelop.setEntity(new StringEntity(request.getRequestBody(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new UnsupportedOperationException("Unsupported rest format: " + request.getObjFormat());
                }
            }
            result = envelop;
        }

        return result;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static final class ResponseHandlerImpl extends BasicResponseHandler {
        private HttpResponse response;

        @Override
        public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
            this.response = response;
            return super.handleResponse(response);
        }
    }

}
