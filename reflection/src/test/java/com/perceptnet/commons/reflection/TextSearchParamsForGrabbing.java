package com.perceptnet.commons.reflection;

import java.util.Date;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 04.09.2017
 */
public class TextSearchParamsForGrabbing extends TextSearchParams<TextSearchParamsForGrabbing> {
    private Date createdAfter;
    private Date createdBefore;

    private Boolean freeAccessBl;
    private Boolean sysOwnershipTakenBl;

    private String uploadedByName;

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public Boolean getFreeAccessBl() {
        return freeAccessBl;
    }

    public void setFreeAccessBl(Boolean freeAccessBl) {
        this.freeAccessBl = freeAccessBl;
    }

    public Boolean getSysOwnershipTakenBl() {
        return sysOwnershipTakenBl;
    }

    public void setSysOwnershipTakenBl(Boolean sysOwnershipTakenBl) {
        this.sysOwnershipTakenBl = sysOwnershipTakenBl;
    }
}
