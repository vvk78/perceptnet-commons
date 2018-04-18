package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.commons.beanprocessing.BaseBeanProcessingNode;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 24.08.2017
 */
public class ConversionNode extends BaseBeanProcessingNode<ConversionNode> {
    private final ConversionContext context;

    private Object srcObj;
    private Object destObj;

    private BeanReflection srcReflection;
    private BeanReflection destReflection;

    public ConversionNode(ConversionContext context, Object srcObj, Object destObj) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        if (srcObj == null) {
            throw new NullPointerException("srcObj is null");
        }
        if (destObj == null) {
            throw new NullPointerException("destObj is null");
        }
        this.context = context;
        this.srcObj = srcObj;
        this.destObj = destObj;
    }

    public ConversionNode(ConversionNode parent, Object srcObj, Object destObj, FieldReflection entryPoint, String entryPointPostfix) {
        super(parent, entryPoint, entryPointPostfix);
        if (srcObj == null) {
            throw new NullPointerException("SrcObj is null");
        }
        if (destObj == null) {
            throw new NullPointerException("DestObj is null");
        }
        this.context = parent.context;
        this.srcObj = srcObj;
        this.destObj = destObj;
    }

    public BeanReflection getSourceReflection() {
        if (srcReflection == null) {
            srcReflection = context.getSrcReflectionProvider().getReflection(srcObj.getClass());
        }
        return srcReflection;
    }

    public BeanReflection getDestReflection() {
        if (destReflection == null) {
            destReflection = context.getDestReflectionProvider().getReflection(getDest().getClass());
        }
        return destReflection;
    }

    public Object getSource() {
        return srcObj;
    }

    public Object getDest() {
        return destObj;
    }




}
