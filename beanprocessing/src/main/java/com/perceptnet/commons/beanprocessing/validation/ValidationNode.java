package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.beanprocessing.BaseBeanProcessingNode;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;

/**
 * created by vkorovkin on 16.05.2018
 */
public class ValidationNode extends BaseBeanProcessingNode<ValidationNode> {

    private final ValidationContext context;

    private Object srcObj;

    private BeanReflection srcReflection;
    private BeanReflection destReflection;

    public ValidationNode(ValidationContext context, Object srcObj, BeanReflection destReflection) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        if (srcObj == null) {
            throw new NullPointerException("srcObj is null");
        }
        if (destReflection == null) {
            throw new NullPointerException("destReflection is null");
        }
        this.context = context;
        this.srcObj = srcObj;
        this.destReflection = destReflection;
    }

    public ValidationNode(ValidationNode parent, Object srcObj, BeanReflection destReflection,
                            FieldReflection entryPoint, String entryPointPostfix) {
        super(parent, entryPoint, entryPointPostfix);
        if (srcObj == null) {
            throw new NullPointerException("SrcObj is null");
        }
        if (destReflection == null) {
            throw new NullPointerException("destReflection is null");
        }
        this.context = parent.context;
        this.srcObj = srcObj;
        this.destReflection = destReflection;
    }

    public BeanReflection getSourceReflection() {
        if (srcReflection == null) {
            srcReflection = context.getSrcReflectionProvider().getReflection(srcObj.getClass());
        }
        return srcReflection;
    }

    public BeanReflection getDestReflection() {
        return destReflection;
    }

    public Object getSource() {
        return srcObj;
    }

}
