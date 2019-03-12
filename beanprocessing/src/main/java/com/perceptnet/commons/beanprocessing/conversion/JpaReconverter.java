package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.abstractions.Identified;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.utils.MapUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base converter to convert DTO back to DO (JPA entity)
 * <p>
 * created by vkorovkin (vkorovkin@gmail.com) on 17.01.2019
 */
public class JpaReconverter<SELF extends JpaReconverter, ID> extends BeanConverter<SELF> {

    protected static final String EXTENDED_ATTRIBUTE_BACK_LINK = "ea.bl";


    private JpaConversionHelper<ID> jpaHelper;

    public JpaReconverter(ConversionContext ctx, JpaConversionHelper<ID> jpaHelper) {
        super(ctx);
        this.jpaHelper = jpaHelper;
    }

    protected void processChildObjectsCollection(FieldReflection destField, FieldReflection srcField) {
        Collection srcItems = (Collection) srcField.getValue(getSource());
        Collection destItems = (Collection) destField.getValue(getDest());
        if (srcItems == null) {
            throw new IllegalStateException("Dto collection field " + srcField + " is null, cannot convert items");
        }

        if (destItems == null) {
            //we require that constructed dests have all collections properly initialized:
            throw new IllegalStateException("Dto collection field " + destField + " is not initialized, cannot convert items");
        }

        int index = -1; //(this index variable is used for logging - to indicate what is child item position)
        Map<ID, Object> destItemsMap = MapUtils.mapById(destItems, new HashMap<>());

        for (Object srcItem : srcItems) {
            index++; //(this index variable is used for logging)
            if (isChildItemToBeSkipped(srcItem)) {
                log.trace("Child source collection item {} at index {} is skipped", srcItem, index);
                continue;
            }

            ID srcItemId = ((Identified<ID>)srcItem).getId();
            Object destItem = destItemsMap.remove(srcItemId);
            if (destItem == null) {
                //new item added in source collection
                destItem = obtainDest(destField.getCollectionItemClass(), srcItem);
            }

            if (isChildItemToBeRecursivelyProcessed(srcItem)) {

//            todo Basically this check is not quite correct - this destination item may be processed differently (as master ereference for example)
//            //Check if item has already been processed:
//            Object destItem = findProcessedDest(srcItem, destField.getCollectionItemClass());
//            if (destItem != null) {
//                destItems.add(destItem);
//                log.trace("Collection field {} in dest {} at idx {} is set to already resolved dest {}, no recursive processing is needed",
//                        destField, getDest(), index, destItem);
//                continue;
//            }

                beforeChildItemRecursiveProcessing(destField, destItem);
                //Attention! Recursive processing:
                getCtx().pushNode(srcItem, destItem, destField, "(" + index + ")");
                try {
                    doProcess();
                } finally {
                    getCtx().popNode();
                }
                destItems.add(destItem);
                if (log.isTraceEnabled()) {
                    log.trace("Collection  {} in dest {} at idx {} is extended with dest {} after recursive processing",
                            destField, getDest(), index, destItem);
                }
            } else {
                destItems.add(destItem);
                if (log.isTraceEnabled()) {
                    log.trace("Collection field {} in dest {} at idx {} is extended with dest {} without recursive processing",
                            destField, getDest(), index, destItem);
                }
            }

            //By now there is only items, whose counterparts have disappeared in source, so we are going to delete them:
            FieldReflection bl = findBackLinkField(destField);
            deleteDestItems(destItemsMap.values(), bl);
        }
    }

    @Override
    protected void beforeCollectionsProcessing() {
        if (getSourceReflection().getCollections().size() > 0) {
            jpaHelper.save(getDest());
        }
    }

    @Override
    protected void afterNodeSuccessfullyProcessed() {
        super.afterNodeSuccessfullyProcessed();
        if (getSourceReflection().getCollections().isEmpty()) {
            jpaHelper.save(getDest());
        }
    }

    @Override
    protected void beforeChildItemRecursiveProcessing(FieldReflection destCollectionField, Object destItem) {
        super.beforeChildItemRecursiveProcessing(destCollectionField, destItem);
        FieldReflection bl = findBackLinkField(destCollectionField);
        if (bl != null) {
            bl.setValue(destItem, getDest());
        }
    }

    @Override
    protected Object obtainDest(Class destClass, Object forSrcItemOrId) {
        if (forSrcItemOrId == null) {
            return null;
        }
        ID id = null;
        if (forSrcItemOrId instanceof Identified) {
            id = ((Identified<ID>) forSrcItemOrId).getId();
        } else {
            try {
                id = (ID) forSrcItemOrId;
            } catch (ClassCastException ignore) {
            }
        }

        if (id != null) {
            return jpaHelper.getOne(id, destClass);
        } else {
            return super.obtainDest(destClass, forSrcItemOrId);
        }
    }

    protected void deleteDestItems(Collection destItems, FieldReflection parentBackLinkField) {
        for (Object destItem : destItems) {
            if (parentBackLinkField != null) {
                parentBackLinkField.setValue(destItem, null);
            }
            jpaHelper.delete(destItem);
        }
    }

    protected void backLinkWithParent(Collection destChildren, FieldReflection parentBackLinkField) {
        if (parentBackLinkField == null) {
            throw new NullPointerException("ParentBackLinkField is null");
        }
        for (Object destChild : destChildren) {
            parentBackLinkField.setValue(destChild, getDest());
        }
    }

    protected FieldReflection findBackLinkField(FieldReflection childCollectionField) {
        Object blCandidate = childCollectionField.getExtendedAttributes().get(EXTENDED_ATTRIBUTE_BACK_LINK);
        if (blCandidate != null && blCandidate instanceof Integer && 0 == ((Integer) blCandidate)) {
            //it actually means back link was searched and not found
            return null;
        }
        FieldReflection bl = (FieldReflection) blCandidate;
        if (bl != null) {
            return bl;
        }

        BeanReflection itemReflection = getCtx().getDestReflectionProvider().getReflection(childCollectionField.getCollectionItemClass());
        List<FieldReflection> list = new ArrayList<>(2);
        for (FieldReflection childField : itemReflection.getReferences().values()) {
            if (childField.getFieldType().equals(getDest().getClass())) {
                list.add(childField);
                if (list.size() > 2) {
                    break;
                }
            }
        }

        if (list.isEmpty()) {
            childCollectionField.getExtendedAttributes().put(EXTENDED_ATTRIBUTE_BACK_LINK, 0);
            return null;
        }

        if (list.size() > 1) {
            //todo      currently I can get needed information about one-to-many many-to-one
            //todo      bi directional mapping from annotations but not from .xbm.xml
            //todo      as *.xbm.xml is in use in the project I am currently developing this tool for,
            //todo      I decided not to waste my time of thorough implementation
            //todo      will be done in future when really needed
            throw new IllegalStateException("Multiple child collections are not supported at the moment");
        }

        bl = list.get(0);
        childCollectionField.getExtendedAttributes().put(EXTENDED_ATTRIBUTE_BACK_LINK, bl);

        return bl;
    }




}
