package com.perceptnet.commons.beanprocessing.inspection;

import com.perceptnet.commons.beanprocessing.BaseBeanProcessor;
import com.perceptnet.commons.beanprocessing.ProcessingContext;
import com.perceptnet.commons.reflection.FieldReflection;

import java.util.Collection;
import java.util.Iterator;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 26.12.2017
 */
public class BeanInspector extends BaseBeanProcessor {

    public BeanInspector(ProcessingContext ctx) {
        super(ctx);
    }

    protected void doProcess() {
        if (!isProcessVisited() && findProcessedObj(getObj()) != null) {
            if (log.isTraceEnabled()) {
                log.trace("Object {} is already processed, skipped", getObj());
            }
            return;
        }

        processId();
        processVersion();
        processFlatFields();

        getCtx().getProcessedObjects().put(createBeanKey(getObj()), getObj());

        processReferences();
        processCollections();
    }


    protected void processId() {
    }

    protected void processVersion() {
    }

    protected void processFlatFields() {
        for (FieldReflection field : getReflection().getFlatFields().values()) {
            Object fieldValue = field.getValue(getObj());
            if (fieldValue == null) {
                processNull(field);
            } else {
                processNonNullFieldValue(field, fieldValue);
            }
        }
    }

    protected void processReferences() {
        for (FieldReflection field : getReflection().getReferences().values()) {
            Object referencedObj = field.getValue(getObj());
            if (referencedObj == null) {
                processNull(field);
            } else {
                //Attention! Recursive processing:
                getCtx().pushNode(referencedObj, field, null);
                try {
                    doProcess();
                } finally {
                    getCtx().popNode();
                }
            }
        }
    }

    protected void processCollections() {
        for (FieldReflection field : getReflection().getCollections().values()) {
            Collection c = (Collection) field.getValue(getObj());
            if (c == null) {
                processNull(field);
            } else {
                processChildObjectsCollection(field, c);
            }
        }
    }

    protected void processChildObjectsCollection(FieldReflection f, Collection c) {
        if (c == null) {
            throw new IllegalStateException("Collection in f " + f + " is null, cannot process items");
        }

        beforeCollectionProcessing(f, c);
        int index = 0;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (o == null) {
                processNull(null);
            } else if (f.isCollectionItemClassFlat()) {
                processNonNullFlatCollectionItem(o);
            } else if (o instanceof Collection) {
                throw new UnsupportedOperationException("Nested collections are not supported yet");
            } else {
                //Attention! Recursive processing:
                getCtx().pushNode(o, f, "(" + index + ")");
                try {
                    doProcess();
                } finally {
                    getCtx().popNode();
                }
            }
            if (it.hasNext()) {
                betweenCollectionItemsProcessing(f);
            }
            index++;
        }
        afterCollectionProcessing(f, c);
    }

    /**
     *
     * @param field may be null if there is no particular field (for example, if it is a collection item)
     */
    protected void processNull(FieldReflection field) {
    }

    protected void processNonNullFieldValue(FieldReflection field, Object fieldValue) {
    }

    protected void processNonNullFlatCollectionItem(Object collectionItem) {
    }

    protected void beforeCollectionProcessing(FieldReflection f, Collection c) {
    }

    protected void afterCollectionProcessing(FieldReflection f, Collection c) {
    }

    protected void betweenCollectionItemsProcessing(FieldReflection f) {
    }

}
