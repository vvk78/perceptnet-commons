package com.perceptnet.commons.beanprocessing;

import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;
import com.perceptnet.commons.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 26.12.2017
 */
public class ProcessingContext<NEP extends NodeExtParams> {
    private ReflectionProvider reflectionProvider;

    private Map<BeanKey, Object> processedObjects = new HashMap<>();

    private ProcessingNode curNode;
    private Class<NEP> nodeExtParamsClass;

    public ProcessingContext(ReflectionProvider reflectionProvider, Class<NEP> nodeExtParamsClass) {
        if (reflectionProvider == null) {
            throw new NullPointerException("SrcReflectionProvider is null");
        }
        this.reflectionProvider = reflectionProvider;
        this.nodeExtParamsClass = nodeExtParamsClass;
    }

    public ReflectionProvider getReflectionProvider() {
        return reflectionProvider;
    }

    public Map<BeanKey, Object> getProcessedObjects() {
        return processedObjects;
    }

    public void setRootNode(Object obj) {
        if (curNode != null) {
            throw new IllegalStateException("Looks like an attempt to set a root node when another one exists already");
        }
        this.curNode = postConstructNode(new ProcessingNode(obj));
    }

    public void pushNode(Object obj, FieldReflection entryPoint, String entryPointPostfix) {
        if (curNode == null) {
            throw new IllegalStateException("An attempt to push a child node when there is no parent");
        }
        curNode = new ProcessingNode(curNode, obj, entryPoint, entryPointPostfix);
        postConstructNode(curNode);
    }

    public void popNode() {
        curNode = curNode.getParent();
    }

    public ProcessingNode curNode() {
        return curNode;
    }

    protected ProcessingNode postConstructNode(ProcessingNode n) {
        if (nodeExtParamsClass != null) {
            n.setExtParams(ClassUtils.createUnsafely(nodeExtParamsClass));
        }
        return n;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * created by vkorovkin (vkorovkin@gmail.com) on 24.08.2017
     */
    public class ProcessingNode extends BaseBeanProcessingNode<ProcessingNode> {

        private Object obj;
        private BeanReflection srcReflection;

        private ProcessingNode(Object obj) {
            if (obj == null) {
                throw new NullPointerException("srcObj is null");
            }
            this.obj = obj;
        }

        private ProcessingNode(ProcessingNode parent, Object obj, FieldReflection entryPoint, String entryPointPostfix) {
            super(parent, entryPoint, entryPointPostfix);
            if (obj == null) {
                throw new NullPointerException("SrcObj is null");
            }
            this.obj = obj;
        }

        public BeanReflection getObjReflection() {
            if (srcReflection == null) {
                srcReflection = ProcessingContext.this.getReflectionProvider().getReflection(obj.getClass());
            }
            return srcReflection;
        }

        public Object getObj() {
            return obj;
        }

    }
}
