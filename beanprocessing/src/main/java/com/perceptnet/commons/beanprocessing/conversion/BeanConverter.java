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

package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.commons.beanprocessing.BeanKey;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.utils.ClassUtils;
import com.perceptnet.commons.utils.EnumUtils;
import com.perceptnet.abstractions.Identified;
import com.perceptnet.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by vkorovkin on 22.03.15.
 */
public class BeanConverter extends BaseConversionProcessor {
    private static final Logger log = LoggerFactory.getLogger(BeanConverter.class);

    public BeanConverter(ConversionContext ctx) {
        super(ctx);
    }

    public <S, D> D process(S srcObj, D destObj) {
        //Push root context
        getCtx().setRootNode(srcObj, destObj);
        doProcess();
        return destObj;
    }

    private void doProcess() {
        if (findProcessedDest(getSource(), getDest().getClass()) != null) {
            if (log.isTraceEnabled()) {
                log.trace("Dto {} is already processed, skipped", getDest());
            }
            return;
        }

        processId();
        processVersion();
        processFlatFields();

        //Put resolved dest, to avoid indefinite recursion while object tree traversing
        final Object dest = getDest();
        if (dest != null) {
            BeanKey destKey = createDestKey(dest);
            if (destKey != null) {
                getCtx().getResolvedDestObjects().put(destKey, getDest());
            }
        }

        processReferences();
        processRefDtoStringDoFields();
        processCollections();
    }

    protected BeanKey createDestKey(Object dest) {
        if (dest == null) {
            return null;
        }
        if (dest instanceof Identified) {
            Object destId = ((Identified) dest).getId();
            if (destId != null) {
                return new BeanKey(destId, dest.getClass());
            }
        }

        return new BeanKey(dest, dest.getClass());
    }

    protected Object findProcessedDest(Object srcObj, Class destClass) {
        if (srcObj == null || destClass == null) {
            return null;
        }
        if (srcObj instanceof Identified) {
            Object srcId = ((Identified) srcObj).getId();
            BeanKey destKey = new BeanKey(srcId, destClass);
            return getCtx().getResolvedDestObjects().get(destKey);
        }

        return null;
    }

    protected void processId() {
        FieldReflection srcIdField = getSourceReflection().getIdField();
        if (srcIdField == null) {
            return;
        }
        FieldReflection destIdField = getDestReflection().getIdField();
        if (destIdField == null) {
            return;
        }
        Object srcId = srcIdField.getValue(getSource());
        destIdField.setValue(getDest(), srcId);
    }

    protected void processVersion() {
        final FieldReflection versionDoField = getSourceReflection().getVersionField();
        final FieldReflection versionDtoField = getDestReflection().getVersionField();
        if (versionDoField != null && versionDtoField != null) {
            versionDtoField.setValue(getDest(), versionDoField.getValue(getSource()));
        }
    }

    protected void processFlatFields() {
        BeanReflection srcRef = getSourceReflection();
        for (FieldReflection destField : getDestReflection().getFlatFields().values()) {
            FieldReflection srcField = srcRef.getFlatFields().get(destField.getFieldName());
            if (srcField != null) {
                if (isFieldGenerallyConvertable(destField, srcField)) {
                    //Special case when enum in dest is related to String in src:
                    if (destField.getFieldType().isEnum() && srcField.getFieldType().equals(String.class)) {
                        destField.setValue(getDest(), EnumUtils.parseUnsafely(destField.getFieldType(), (String) srcField.getValue(getSource())));
//                    } else if (LookupDto.class.isAssignableFrom(getDestReflection().getBeanClass())
//                            && destField.getFieldName().equals("code")
//                            && !String.class.equals(srcField.getFieldType())) {
//                        Object srcValue = srcField.getValue(getSource());
//                        destField.setValue(getDest(), srcValue == null ? null : srcValue.toString());
                    } else {
                        destField.setValue(getDest(), srcField.getValue(getSource()));
                    }

                    log.trace("Field {} copied to dest {} from src {}", destField.getFieldName(), getDest(), getSource());
                }
            }
            if (processFlatDestRefSrcMapping(destField)) {
                if (log.isTraceEnabled()) {
                    log.trace("Flat field {} in dest {} copied from referenced object in src {}",
                                                            destField.getFieldName(), getDest(), getSource());
                }
                continue;
            }
        }
    }

    protected void processRefDtoStringDoFields() {
        BeanReflection srcRef = getSourceReflection();

        for (FieldReflection destField : getDestReflection().getReferences().values()) {
            FieldReflection srcField = srcRef.getFlatFields().get(destField.getFieldName());
            if (isFieldGenerallyConvertable(destField, srcField)) {
                if (srcField.getFieldType().equals(String.class)) {
                    destField.setValue(getDest(), ClassUtils.createUnsafely(destField.getFieldType(), srcField.getValue(getSource())));
                }
            }
        }
    }

    /**
     * Id destination field can be processed as
     */
    private boolean processFlatDestRefSrcMapping(FieldReflection destField) {
        String refFieldName = StringUtils.cutOffTail(destField.getFieldName(), ID_FIELD_NAME_POSTFIX);
        if (refFieldName == null || refFieldName.isEmpty()) {
            return false;
        }

        FieldReflection srcRefField = getSourceReflection().getReferences().get(refFieldName);
        if (srcRefField == null || srcRefField.getFieldKind() != FieldReflection.Kind.REFERENCE) {
            return false;
        }

        Object referencedDo = srcRefField.getValue(getSource());
        if (referencedDo == null) {
            destField.setValue(getDest(), null);
        } else {
            if (referencedDo instanceof Identified) {
                Object referencedId = ((Identified) referencedDo).getId();
                destField.setValue(getDest(), referencedId);
            }
        }

        //destField.setValue(getDest(), referencedDo != null ? referencedDo.getId() : null);
        return true;
    }

    private boolean isFieldGenerallyConvertable(FieldReflection destField, FieldReflection srcField) {
        if (destField == null) {
            //basically it should never happen, but just in case...
            log.trace("Dto field is null. Skipped");
            return false;
        }

        if (srcField == null) {
            log.trace("Field {} present in {} but is missing in data object {}", destField.getFieldName(),
                    getDest().getClass().getSimpleName(), getSource().getClass().getSimpleName());
            return false;
        }
        if (srcField.isWriteOnly()) {
            log.trace("Field {} in src {} is write only, skipped",
                    destField.getFieldName(), getSource().getClass().getSimpleName());
            return false;
        }
        if (destField.isReadOnly()) {
            log.trace("Field {} in dest {} is readonly only, skipped",
                    destField.getFieldName(), getSource().getClass().getSimpleName());
            return false;
        }
        return true;
    }

    protected void processReferences() {
        BeanReflection srcRef = getSourceReflection();
        for (FieldReflection destField : getDestReflection().getReferences().values()) {
            FieldReflection srcField = srcRef.getReferences().get(destField.getFieldName());
            if (isFieldGenerallyConvertable(destField, srcField)) {
                Object referencedDo = srcField.getValue(getSource());
                if (referencedDo == null) {
                    destField.setValue(getDest(), null);
                    log.trace("Reference field {} in dest {} is set to null, as corresponding reference in src {} is null",
                            destField, getDest(), getSource());
                    continue;
                }

                Object referencedDto = findProcessedDest(referencedDo, destField.getFieldType());
                if (referencedDto != null) {
                    destField.setValue(getDest(), referencedDto);
                    log.trace("Reference field {} in dest {} is set to already resolved dest {}, no recursive processing is needed",
                            destField, getDest(), referencedDto);
                    continue;
                }


                referencedDto = ClassUtils.createUnsafely(destField.getFieldType());

                //Attention! Recursive processing:
                getCtx().pushNode(referencedDo, referencedDto, destField, null);
                try {
                    doProcess();
                } finally {
                    getCtx().popNode();
                }

                destField.setValue(getDest(), referencedDto);
                log.trace("Reference field {} in dest {} is set to dest {} after recursive processing", destField, getDest(), referencedDto);
            }
        }

    }

    protected void processCollections() {
        BeanReflection srcRef = getSourceReflection();
        for (FieldReflection destField : getDestReflection().getCollections().values()) {
            FieldReflection srcField = srcRef.getCollections().get(destField.getFieldName());
            if (srcField == null) {
                log.trace("No collection field {} in src {}, skipped", destField, getSource());
                continue;
            }

            if (destField.isCollectionItemClassFlat()) {
                processFlatItemsCollection(destField, srcField);
            } else {
                processChildObjectsCollection(destField, srcField);
            }
        }
    }

    private void processChildObjectsCollection(FieldReflection destField, FieldReflection srcField) {
        Collection srcItems = (Collection) srcField.getValue(getSource());
        Collection destItems = (Collection) destField.getValue(getDest());
        if (srcItems == null) {
            throw new IllegalStateException("Dto collection field " + srcField + " is null, cannot convert items");
        }

        if (destItems == null) {
            //we require that constructed dests have all collections properly initialized:
            throw new IllegalStateException("Dto collection field " + destField + " is not initialized, cannot convert items");
        }

        int index = 0;
        destItems.clear();
        for (Object srcItem : srcItems) {
            Object destItem = findProcessedDest(srcItem, destField.getCollectionItemClass());
            if (destItem != null) {
                destItems.add(destItem);
                log.trace("Collection field {} in dest {} at idx {} is set to already resolved dest {3}, no recursive processing is needed",
                        destField, getDest(), index, destItem);
            } else {
                destItem = ClassUtils.createUnsafely(destField.getCollectionItemClass());

                //Attention! Recursive processing:
                getCtx().pushNode(srcItem, destItem, destField, "(" + index + ")");
                try {
                    doProcess();
                } finally {
                    getCtx().popNode();
                }

                destItems.add(destItem);
                log.trace("Collection field {} in dest {} at idx {} is set to dest {} after recursive processing",
                        destField, getDest(), index, destItem);

            }
            index++;
        }
    }

    private void processFlatItemsCollection(FieldReflection destField, FieldReflection srcField) {
        Collection srcItems = (Collection) srcField.getValue(getSource());
        Collection destItems = (Collection) destField.getValue(getDest());

        if (srcItems == null) {
            throw new IllegalStateException("Dto collection field " + srcField + " is null, cannot convert items");
        }

        if (destItems == null) {
            //we require that constructed dests have all collections properly initialized:
            throw new IllegalStateException("Dto collection field " + destField + " is not initialized, cannot convert items");
        }

        destItems.clear();
        destItems.addAll(srcItems);
    }


}
