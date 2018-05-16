package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.beanprocessing.conversion.BeanConverter;
import com.perceptnet.commons.reflection.BeanReflection;
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

//    public void process() {
//        processFlatFields();
//        processReferences();
//        processCollections();
//    }
//
//
//    protected void processFlatFields() {
//        BeanReflection destRef = getDestReflection();
//        for (FieldReflection srcField : getSourceReflection().getFlatFields().values()) {
//            FieldReflection destField = destRef.getFlatFields().get(srcField.getFieldName());
//            if (destField != null) {
//                if (isFieldValidatable(srcField, destField)) {
//                    //Special case when enum in Src is related to String in DEST:
//                    Object srcValue = srcField.getValue(getSource());
//                    FieldValidator fieldValidator = getFieldValidator(destField);
//                    //to handle special case when a enum in SRC in mapped to String in DEST:
//                    if (srcValue != null && srcValue.getClass().isEnum() && String.class.equals(destField.getFieldType())) {
//                        srcValue = srcValue.toString();
//                    }
//
//                    fieldValidator.validateFieldValue(getCtx(), getSource(), srcValue);
//
//                }
//            } else if (processFlatSrcRefDestMapping(srcField)) {
//                log.debug("Flat field {} id src {} validated as referenced object in dest {}",
//                        srcField.getFieldName(), getSource(), getDestClass().getSimpleName());
//                continue;
//            }
//        }
//    }
//
//    private boolean processFlatSrcRefDestMapping(FieldReflection srcField) {
//        if (!Long.class.equals(srcField.getFieldType())) {
//            return false;
//        }
//
//        String refFieldName = StringUtils.cutOffTail(srcField.getFieldName(), ID_FIELD_NAME_POSTFIX);
//        if (refFieldName == null || refFieldName.isEmpty()) {
//            return false;
//        }
//
//        FieldReflection destRefField = getDestReflection().getReferences().get(refFieldName);
//        if (destRefField == null || !DTO.class.isAssignableFrom(destRefField.getFieldType())) {
//            return false;
//        }
//
//        Long referencedDestId = (Long) srcField.getValue(getSource());
//        FieldValidator fieldValidator = getFieldValidator(destRefField);
//        fieldValidator.validateFieldValue(getCtx(), getSource(), referencedDestId);
//
//        return true;
//    }
//
//    protected void processReferences() {
//        BeanReflection destRef = getDestReflection();
//        for (FieldReflection srcField : getSourceReflection().getReferences().values()) {
//            FieldReflection destField = destRef.getReferences().get(srcField.getFieldName());
//            if (!isFieldConvertable(srcField, destField)) {
//                continue;
//            }
//
//            BaseIdentifiedSrc referencedSrc = (BaseIdentifiedSrc) srcField.getValue(getSrc());
//            if (isFieldValidatable(srcField, destField)) {
//                FieldValidator fieldValidator = getFieldValidator(destField);
//                fieldValidator.validateFieldValue(getCtx(), getSrc(), referencedSrc);
//            }
//
//            if (referencedSrc == null) {
//                log.debug("Reference field {} in src {} is null, no recursive processing", srcField, getSrc());
//                continue;
//            }
//
//            //dest recursive processing if needed:
//            if (BaseUpdatableSrc.class.isAssignableFrom(referencedSrc.getClass())) {
//                //Attention! Recursive processing:
//                pushContext(new SrcValidationContext(getCtx(), destField, null, referencedSrc));
//                try {
//                    log.trace("Recursive reference processing at {}", getCtx().getPath());
//                    destProcess();
//                } finally {
//                    popContext();
//                }
//            }
//        }
//    }
//
//    protected void processCollections() {
//        BeanReflection destRef = getDestReflection();
//        for (FieldReflection srcField : getSrcReflection().getCollections().values()) {
//            FieldReflection destField = destRef.getCollections().get(srcField.getFieldName());
//            if (destField == null) {
//                log.debug("No collection field {} in dest {}, skipped", srcField);
//                continue;
//            }
//
//            if (BeanReflectionBuilder.isFlatType(srcField.getCollectionItemClass())) {
//                //processFlatItemsCollection(srcField, destField);
//            } else {
//                processChildESTbjectsCollection(srcField, destField);
//            }
//        }
//    }
//
//    private void processChildESTbjectsCollection(FieldReflection srcField, FieldReflection destField) {
//        if (!BaseUpdatableSrc.class.isAssignableFrom(srcField.getCollectionItemClass())) {
//            log.debug("Collection field {} in src {} is not updatable, skipped", srcField, getSrc());
//            return;
//        }
//
//        int index = 0;
//        Collection<BaseIdentifiedSrc> srcItems = (Collection<BaseIdentifiedSrc>) srcField.getValue(getSrc());
//        for (BaseIdentifiedSrc srcItem : srcItems) {
//            if (srcItem == null) {
//                log.debug("Null item is encountered in collection {} at {}, ignored", srcField, getCtx().getPath());
//                continue;
//            }
//
//            //Attention! Recursive processing:
//            pushContext(new SrcValidationContext(getCtx(), destField, "(" + index + ")", srcItem));
//            try {
//                destProcess();
//            } finally {
//                popContext();
//            }
//
//            index++;
//        }
//    }
//
//
//    private void processFlatItemsCollection(FieldReflection srcField, FieldReflection destField) {
////        Collection srcItems = (Collection) srcField.getValue(getSrc());
////
////        if (srcItems == null) {
////            //we require that constructed SRCs have all collections properly initialized:
////            throw new IllegalStateException("Src collection field " + srcField + " is not initialized, cannot convert items");
////        }
////
//    }
//
//
//    private boolean isFieldValidatable(FieldReflection srcField, FieldReflection destField) {
//        if (!isFieldConvertable(srcField, destField)){
//            return false;
//        }
//
//        if (getFieldValidator(destField) == null) {
//            log.warn("Data object field {} has no validator, skipped {}", destField);
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean isFieldConvertable(FieldReflection srcField, FieldReflection destField) {
//        if (srcField == null) {
//            //basically it should never happen, but just in case...
//            log.warn("Src field is null. Skipped");
//            return false;
//        }
//
//        if (destField == null) {
//            log.warn("Field {} present in {} but is missing in data object {}", srcField.getFieldName(),
//                    getSource().getClass().getSimpleName(), getDestClass().getSimpleName());
//            return false;
//        }
//
//        if (srcField.isWriteOnly()) {
//            log.debug("Field {} in src {} is write only, skipped",
//                    srcField.getFieldName(), getSource().getClass().getSimpleName());
//            return false;
//        }
//        if (destField.isReadOnly()) {
//            log.debug("Field {} in dest {} is read only, skipped",
//                    destField.getFieldName(), getDestClass().getSimpleName());
//            return false;
//        }
//
//        return true;
//    }
//
//    private Class getDestClass() {
//        return getDestReflection().getBeanClass();
//    }
//
//    private FieldValidator getFieldValidator(FieldReflection destField) {
//        throw new UnsupportedOperationException("Not implemented yet");
//    }


}
