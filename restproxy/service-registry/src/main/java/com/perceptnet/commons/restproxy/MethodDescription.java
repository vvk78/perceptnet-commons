package com.perceptnet.commons.restproxy;


import com.perceptnet.commons.json.parsing.ObjectInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 25.01.2018
 */
class MethodDescription {
    private final Method method;
    /**
     * Basically this is used only at client side
     */
    private String serviceName;
    private List<ObjectInfo> params;
    private ObjectInfo resultInfo;
    private boolean lastNotFlat;
    private boolean allParamsFromBody;


    public MethodDescription(Method method, ObjectInfo resultInfo,
                             List<ObjectInfo> params, boolean lastNotFlat, boolean allParamsFromBody) {
        if (method == null) {
            throw new NullPointerException("Method is null");
        }
        this.method = method;
        this.resultInfo = resultInfo;
        this.params = params;
        this.lastNotFlat = lastNotFlat;
        this.allParamsFromBody = allParamsFromBody;
    }

    public Method getMethod() {
        return method;
    }

    public ObjectInfo getResultInfo() {
        return resultInfo;
    }

    public List<ObjectInfo> getParams() {
        return params;
    }

    public void setParams(List<ObjectInfo> params) {
        this.params = params;
    }

    public boolean isLastNotFlat() {
        return lastNotFlat;
    }

    public boolean isAllParamsFromBody() {
        return allParamsFromBody;
    }

    public String getServiceName() {
        return serviceName;
    }

    public MethodDescription setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
}
