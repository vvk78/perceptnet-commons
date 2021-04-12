package com.perceptnet.commons.json.parsing;

import com.perceptnet.commons.beanprocessing.NodeExtParams;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.utils.SimpleTypeInfo;

import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 12.01.2018
 */
public class ParsingNodeParams implements NodeExtParams {
    private FieldReflection curField;
    private String lastStringValue;
    private Object lastValue;
    private SimpleTypeInfo nextExpectedItem;
    /**
     * In case the current node is a collection with mixed item types, this field keeps list of expected item infos. In other case it is null;
     */
    private List<? extends SimpleTypeInfo> expectedCollectionItems;
    private int nextExpectedCollectionItemIdx;

    public FieldReflection getCurField() {
        return curField;
    }

    public void setCurField(FieldReflection curField) {
        this.curField = curField;
    }


    public SimpleTypeInfo getNextExpectedItem() {
        return nextExpectedItem;
    }

    public void setNextExpectedItem(SimpleTypeInfo nextExpectedItem) {
        this.nextExpectedItem = nextExpectedItem;
    }

    public Object pollLastValue() {
        Object result = getLastValue();
        lastValue = null;
        lastStringValue = null;
        return result;
    }

    public String getLastStringValue() {
        return lastStringValue;
    }

    public void setLastValue(Object lastValue) {
        this.lastValue = lastValue;
    }

    public Object getLastValue() {
        return lastValue;
    }

    public void setLastStringValue(String lastStringValue) {
        this.lastStringValue = lastStringValue;
        this.lastValue = lastStringValue;
    }

    public String pollLastStringValue() {
        String result = lastStringValue;
        lastStringValue = null;
        return result;
    }

    public void setExpectedCollectionItems(List<? extends SimpleTypeInfo> expectedCollectionItems) {
        this.expectedCollectionItems = expectedCollectionItems;
        this.nextExpectedCollectionItemIdx = 0;
    }

    public SimpleTypeInfo pollNextExpectedCollectionItemInfo() {
        if (expectedCollectionItems == null || nextExpectedCollectionItemIdx >= expectedCollectionItems.size()) {
            return null;
        }
        return expectedCollectionItems.get(nextExpectedCollectionItemIdx++);
    }

    public List<? extends SimpleTypeInfo> getExpectedCollectionItems() {
        return expectedCollectionItems;
    }
}
