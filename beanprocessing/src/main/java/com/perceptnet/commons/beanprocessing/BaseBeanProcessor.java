package com.perceptnet.commons.beanprocessing;

import com.perceptnet.abstractions.Identified;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 12.01.2018
 */
public class BaseBeanProcessor<NEP extends NodeExtParams> {
    protected Logger log = LoggerFactory.getLogger(getClass());

    private ProcessingContext ctx;

    public BaseBeanProcessor(ProcessingContext ctx) {
        this.ctx = ctx;
    }

    public BaseBeanProcessor() {
    }

    public <S> S process(S obj) {
        //Push root context
        getCtx().setRootNode(obj);
        doProcess();
        return obj;
    }

    protected void doProcess() {
    }

    protected void setCtx(ProcessingContext ctx) {
        this.ctx = ctx;
    }

    protected ProcessingContext getCtx() {
        return ctx;
    }

    protected Object getObj() {
        return curNode().getObj();
    }

    protected ProcessingContext.ProcessingNode curNode() {
        return getCtx().curNode();
    }

    protected FieldReflection getCurEntryPoint() {
        return curNode().getEntryPoint();
    }

    protected BeanReflection getReflection() {
        return curNode().getObjReflection();
    }


    protected BeanKey createBeanKey(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Identified) {
            Object id = ((Identified) obj).getId();
            BeanKey key = new BeanKey(id, obj.getClass());
            return key;
        } else {
            return new BeanKey(obj, obj.getClass());
        }
    }

    protected Object findProcessedObj(Object obj) {
        if (obj == null) {
            return null;
        }
        BeanKey key = createBeanKey(obj);
        if (key != null) {
            return getCtx().getProcessedObjects().get(key);
        }

        return null;
    }

    /**
     * Returns true if default processing still needs to be done for already inspected object.
     */
    protected boolean isProcessVisited() {
        return false;
    }

    /**
     * Returns current node extended params
     */
    protected NEP nep() {
        ProcessingContext.ProcessingNode curNode = curNode();
        if (curNode == null) {
            return null;
        }
        return (NEP) curNode.getExtParams();
    }

}
