package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.abstractions.Updatable;
import com.perceptnet.commons.beanprocessing.conversion.BeanConverter;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.BeanReflectionBuilder;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.utils.StringUtils;
import com.perceptnet.commons.validation.FieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.perceptnet.commons.beanprocessing.Const.ID_FIELD_NAME_POSTFIX;

/**
 * Validator destined to validate SRCs (src) using validation rules defined for DESTs (Dest).
 *
 * (SRC - Data Transfer Object, DEST - Data Object)
 *
 * created by vkorovkin on 16.05.2018
 */
public class SrcByDestValidator extends BaseValidationProcessor {
    private static final Logger log = LoggerFactory.getLogger(SrcByDestValidator.class);

    public SrcByDestValidator(ValidationContext ctx) {
        super(ctx);
    }

    public void process() {
        doProcess();
    }

    protected void doProcess() {
        processFlatFields();
        processReferences();
        processCollections();
    }


    protected void processFlatFields() {
        BeanReflection destRef = getDestReflection();
        for (FieldReflection srcField : getSourceReflection().getFlatFields().values()) {
            FieldReflection destField = destRef.getFlatFields().get(srcField.getFieldName());
            if (destField != null) {
                if (isFieldValidatable(srcField, destField)) {
                    FieldValidator fieldValidator = getFieldValidator(destField);
                    if (fieldValidator == null) {
                        log.warn("Data object field {} has no validator, skipped {}", destField);
                        continue;
                    }
                    //Special case when enum in Src is related to String in DEST:
                    Object srcValue = srcField.getValue(getSource());

                    //to handle special case when a enum in SRC in mapped to String in DEST:
                    if (srcValue != null && srcValue.getClass().isEnum() && String.class.equals(destField.getFieldType())) {
                        srcValue = srcValue.toString();
                    }

                    fieldValidator.validateFieldValue(getCtx(), getSource(), srcValue);

                }
            } else if (processFlatSrcRefDestMapping(srcField)) {
                log.debug("Flat field {} id src {} validated as referenced object in dest {}",
                        srcField.getFieldName(), getSource(), getDestClass().getSimpleName());
                continue;
            }
        }
    }

    private boolean processFlatSrcRefDestMapping(FieldReflection srcField) {
        if (!Long.class.equals(srcField.getFieldType())) {
            return false;
        }

        String refFieldName = StringUtils.cutOffTail(srcField.getFieldName(), ID_FIELD_NAME_POSTFIX);
        if (refFieldName == null || refFieldName.isEmpty()) {
            return false;
        }

        FieldReflection destRefField = getDestReflection().getReferences().get(refFieldName);
        if (destRefField == null) {
            return false;
        }

        Long referencedDestId = (Long) srcField.getValue(getSource());
        FieldValidator fieldValidator = getFieldValidator(destRefField);
        fieldValidator.validateFieldValue(getCtx(), getSource(), referencedDestId, srcField);

        return true;
    }

    protected void processReferences() {
        BeanReflection destRef = getDestReflection();
        for (FieldReflection srcField : getSourceReflection().getReferences().values()) {
            FieldReflection destField = destRef.getReferences().get(srcField.getFieldName());
            if (!isFieldConvertable(srcField, destField)) {
                continue;
            }

            Object referencedSrc = srcField.getValue(getSource());
            if (isFieldValidatable(srcField, destField)) {
                FieldValidator fieldValidator = getFieldValidator(destField);
                if (fieldValidator == null) {
                    continue;
                }
                fieldValidator.validateFieldValue(getCtx(), srcField, getSource(), referencedSrc);
            }

            if (referencedSrc == null) {
                log.debug("Reference field {} in src {} is null, no recursive processing", srcField, getSource());
                continue;
            }

            //dest recursive processing if needed (Updatatble is a marker we need to recurse into):
            if (Updatable.class.isAssignableFrom(referencedSrc.getClass())) {
                //Attention! Recursive processing:
                getCtx().pushNode(referencedSrc, destField.getFieldType(), srcField, null);
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("Recursive reference processing at {}", getCtx().getCurNode().getPath());
                    }
                    doProcess();
                } finally {
                    getCtx().popNode();
                }
            }
        }
    }

    protected void processCollections() {
        BeanReflection destRef = getDestReflection();
        for (FieldReflection srcField : getSourceReflection().getCollections().values()) {
            FieldReflection destField = destRef.getCollections().get(srcField.getFieldName());
            if (destField == null) {
                log.debug("No collection field {} in dest {}, skipped", srcField);
                continue;
            }

            if (srcField.isCollectionItemClassFlat()) {
                //processFlatItemsCollection(srcField, destField);
            } else {
                processChildObjectsCollection(srcField, destField);
            }
        }
    }

    private void processChildObjectsCollection(FieldReflection srcField, FieldReflection destField) {
        if (!Updatable.class.isAssignableFrom(srcField.getCollectionItemClass())) {
            log.debug("Collection field {} in src {} is not updatable, skipped", srcField, getSource());
            return;
        }

        int index = 0;
        Collection srcItems = (Collection) srcField.getValue(getSource());
        for (Object srcItem : srcItems) {
            if (srcItem == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Null item is encountered in collection {} at {}, ignored", srcField, getCtx().getCurNode().getPath());
                }
                continue;
            }

            //Attention! Recursive processing:
            getCtx().pushNode(srcItem, destField.getCollectionItemClass(), srcField, "(" + index + ")");
            try {
                if (log.isTraceEnabled()) {
                    log.trace("Recursive child item processing at {}", getCtx().getCurNode().getPath());
                }
                doProcess();
            } finally {
                getCtx().popNode();
            }

            index++;
        }
    }


    private void processFlatItemsCollection(FieldReflection srcField, FieldReflection destField) {
//        Collection srcItems = (Collection) srcField.getValue(getSrc());
//
//        if (srcItems == null) {
//            //we require that constructed SRCs have all collections properly initialized:
//            throw new IllegalStateException("Src collection field " + srcField + " is not initialized, cannot convert items");
//        }
//
    }


    private boolean isFieldValidatable(FieldReflection srcField, FieldReflection destField) {
        if (!isFieldConvertable(srcField, destField)){
            return false;
        }

        return true;
    }

    private boolean isFieldConvertable(FieldReflection srcField, FieldReflection destField) {
        if (srcField == null) {
            //basically it should never happen, but just in case...
            log.warn("Src field is null. Skipped");
            return false;
        }

        if (destField == null) {
            log.warn("Field {} present in {} but is missing in data object {}", srcField.getFieldName(),
                    getSource().getClass().getSimpleName(), getDestClass().getSimpleName());
            return false;
        }

        if (srcField.isWriteOnly()) {
            log.debug("Field {} in src {} is write only, skipped",
                    srcField.getFieldName(), getSource().getClass().getSimpleName());
            return false;
        }
        if (destField.isReadOnly()) {
            log.debug("Field {} in dest {} is read only, skipped",
                    destField.getFieldName(), getDestClass().getSimpleName());
            return false;
        }

        return true;
    }

    private Class getDestClass() {
        return getDestReflection().getBeanClass();
    }

    private FieldValidator getFieldValidator(FieldReflection destField) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
