package com.perceptnet.restclient;

/**
 * Takes care of firing events around rest caller invocation.
 *
 * created by vkorovkin (vkorovkin@gmail.com) on 13.02.2018
 */
public class RestCallerInvocationEventWrapper {

    protected RestCallEventListener eventListener;
    protected RestCaller restCaller;

    private final boolean trackCallMilestonesMs;
    private volatile long lastCallAttemptMs;
    private volatile long lastCallSuccessMs;
    private volatile long lastCallFailureMs;

    public RestCallerInvocationEventWrapper(RestCaller restCaller) {
        this(restCaller, null);
    }

    public RestCallerInvocationEventWrapper(RestCaller restCaller, RestCallEventListener eventListener) {
        this(restCaller, null, false);
        if (restCaller == null) {
            throw new NullPointerException("RestCaller is null");
        }
        this.restCaller = restCaller;
    }

    public RestCallerInvocationEventWrapper(RestCaller restCaller, RestCallEventListener eventListener, boolean trackCallMilestonesMs) {
        if (restCaller == null) {
            throw new NullPointerException("RestCaller is null");
        }
        this.restCaller = restCaller;
        this.trackCallMilestonesMs = trackCallMilestonesMs;
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

    public String doInvokeRest(RestRequest request) {
        String rawResponse;
        fireBeforeCallEvent();
        try {
            if (trackCallMilestonesMs) {
                lastCallAttemptMs = System.currentTimeMillis();
            }
            //--- Call:
            rawResponse = restCaller.invokeRest(request);
            if (trackCallMilestonesMs) {
                lastCallSuccessMs = System.currentTimeMillis();
            }
        } catch (Throwable t) {
            if (trackCallMilestonesMs) {
                lastCallFailureMs = System.currentTimeMillis();
            }
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
            if (trackCallMilestonesMs) {
                lastCallAttemptMs = System.currentTimeMillis();
            }
            //--- Call:
            rawResponse = restCaller.invokeRestForBytes(request);
            if (trackCallMilestonesMs) {
                lastCallSuccessMs = System.currentTimeMillis();
            }
        } catch (Throwable t) {
            if (trackCallMilestonesMs) {
                lastCallFailureMs = System.currentTimeMillis();
            }
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

    public boolean isTrackCallMilestonesMs() {
        return trackCallMilestonesMs;
    }

    public long getLastCallAttemptMs() {
        return lastCallAttemptMs;
    }

    public long getLastCallSuccessMs() {
        return lastCallSuccessMs;
    }

    public long getLastCallFailureMs() {
        return lastCallFailureMs;
    }
}
