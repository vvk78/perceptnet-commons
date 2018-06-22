package com.perceptnet.restclient;

/**
 * Takes care of firing events around rest caller invocation.
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 13.02.2018
 */
public class RestCallerInvocationEventWrapper {
    protected RestCallEventListener eventListener;
    protected RestCaller restCaller;

    public RestCallerInvocationEventWrapper(RestCaller restCaller) {
        this(restCaller, null);
    }

    public RestCallerInvocationEventWrapper(RestCaller restCaller, RestCallEventListener eventListener) {
        if (restCaller == null) {
            throw new NullPointerException("RestCaller is null");
        }
        this.restCaller = restCaller;
    }

    public RestCallEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(RestCallEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public RestCaller getRestCaller() {
        return restCaller;
    }

    String doInvokeRest(RestRequest request) {
        String rawResponse;
        fireBeforeCallEvent();
        try {
            rawResponse = restCaller.invokeRest(request);
        } catch (Throwable t) {
            fireAfterCallErrorEvent(t);
            throw t;
        }
        fireAfterCallSuccessEvent();
        return rawResponse;
    }

    protected byte[] doInvokeRestForBytes(RestRequest request) {
        byte[] rawResponse;
        fireBeforeCallEvent();
        try {
            rawResponse = restCaller.invokeRestForBytes(request);
        } catch (Throwable t) {
            fireAfterCallErrorEvent(t);
            throw t;
        }
        fireAfterCallSuccessEvent();
        return rawResponse;
    }

    private void fireBeforeCallEvent() {
        if (eventListener == null) {
            return;
        }
        try {
            eventListener.beforeCall();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void fireAfterCallErrorEvent(Throwable t) {
        if (eventListener == null) {
            return;
        }
        try {
            eventListener.afterCallError(t);
        } catch (Throwable t2) {
            t2.printStackTrace();
        }
    }

    private void fireAfterCallSuccessEvent() {
        if (eventListener == null) {
            return;
        }
        try {
            eventListener.afterCallSuccess();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
