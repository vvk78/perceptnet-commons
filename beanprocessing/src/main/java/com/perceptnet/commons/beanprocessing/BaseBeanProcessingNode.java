/*
 * Copyright 2017 Perceptnet
 *
 * This source code is Perceptnet Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 *
 */

package com.perceptnet.commons.beanprocessing;


import com.perceptnet.commons.reflection.FieldReflection;

/**
 * Created by vkorovkin on 20.04.2015.
 */
public class BaseBeanProcessingNode<SELF extends BaseBeanProcessingNode> {

    /**
     * Parent node we entered this node from.
     */
    private SELF parent;

    private FieldReflection entryPoint;
    private String entryPointPostfix; //extra entry point name (for instance, helping to indicate position in collections)
    private NodeExtParams extParams; //may be null

    public BaseBeanProcessingNode() {
    }

    protected BaseBeanProcessingNode(SELF parent, FieldReflection entryPoint, String entryPointPostfix) {
        this.entryPoint = entryPoint;
        this.entryPointPostfix = entryPointPostfix;
        this.parent = parent;
    }

    public SELF getParent() {
        return parent;
    }

    public String getEntryPointName() {
        if (entryPoint == null) {
            return "";
        }
        String result = entryPoint.getFieldName();
        if (entryPointPostfix != null) {
            result = result + entryPointPostfix;
        }

        return result;
    }

    public FieldReflection getEntryPoint() {
        return entryPoint;
    }

    /**
     * Returns path string indicating the processing step in the object graph. May be used in error messages etc.
     */
    public String getPath() {
        if (getParent() == null) {
            return getEntryPointName();
        }

        String base = getParent().getPath();
        if (base == null || base.trim().isEmpty()) {
            return getEntryPointName();
        }

        return base + "." + getEntryPointName();
    }

    NodeExtParams getExtParams() {
        return extParams;
    }

    void setExtParams(NodeExtParams extParams) {
        this.extParams = extParams;
    }
}
