package com.perceptnet.commons.json.parsing;


import com.perceptnet.commons.beanprocessing.BaseBeanProcessor;
import com.perceptnet.commons.beanprocessing.ProcessingContext;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;
import com.perceptnet.commons.utils.CastUtils;
import com.perceptnet.commons.utils.ClassUtils;
import com.perceptnet.commons.utils.ParseUtils;
import com.perceptnet.commons.utils.StringEscapeUtils;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 24.11.2017
 */
public class BaseSimpleJsonParser extends BaseBeanProcessor<ParsingNodeParams> {
    private static final Object IGNORE = new Object();
    private static final Object NULL = new Object();

    private ReflectionProvider reflectionProvider;
    private List<? extends ObjectInfo> expectedTopLevelItems;
    private int nextExpectedTopLevelClassIdx;
    private boolean resetContextOnFinish;

    private List parsedTopLevelObjects;
    private int skipLevel;

    private boolean strictExpectedObjects;

    public List getParsedTopLevelObjects() {
        return parsedTopLevelObjects;
    }

    public void setExpectedTopLevelItems(ObjectInfo ... expectedTopLevelItems) {
        setExpectedTopLevelItems(Arrays.asList(expectedTopLevelItems));
    }

    public void setExpectedTopLevelItems(List<? extends ObjectInfo> expectedTopLevelItems) {
        this.expectedTopLevelItems = expectedTopLevelItems;
        this.nextExpectedTopLevelClassIdx = 0;
    }

    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public void setStrictExpectedObjects(boolean strictExpectedObjects) {
        this.strictExpectedObjects = strictExpectedObjects;
    }

    public void startParse(int ln, int col) {
        if (getCtx() == null) {
            setCtx(new ProcessingContext(reflectionProvider, ParsingNodeParams.class));
            resetContextOnFinish = true;
        }
        parsedTopLevelObjects = expectedTopLevelItems != null ? new ArrayList(expectedTopLevelItems.size()) : new ArrayList();
        getCtx().setRootNode(parsedTopLevelObjects);
        nep().setExpectedCollectionItems(expectedTopLevelItems);
    }

    public void finishParse(int ln, int col) {
        ParsingNodeParams nep = nep();
        if (nep != null) {
            Object o = nep.pollLastValue();
            if (o == NULL) {
                parsedTopLevelObjects.add(null);
            } else if (o == IGNORE) {
                if (expectedTopLevelItems != null) {
                    throw new IllegalStateException("Type mismatch");
                }
            } else if (o != null) {
                parsedTopLevelObjects.add(o);
            }
        }
        ProcessingContext ctx = getCtx();
        if (ctx.curNode() != null) {
            ctx.popNode();
        }

        if (resetContextOnFinish) {
            setCtx(null);
        }
        if (strictExpectedObjects && expectedTopLevelItems != null && expectedTopLevelItems.size() != parsedTopLevelObjects.size()) {
            throw new RuntimeException("Not all expected objects parsed " + expectedTopLevelItems.size() + " != " + parsedTopLevelObjects.size());
        }
    }

    public void startObject(String className, int ln, int col) {
        if (className != null) {
            className = unquote(className);
            if (className.trim().isEmpty()) {
                className = null;
            }
        }

        out("startObject " + className +" [ln: " + ln + ", col: " + col +"]");
        if (skipLevel > 0) {
            incSkipLevel();
            return;
        }

        if (className == null) {
            ObjectInfo expectedItem = expectedItem();
            if (expectedItem == null) {
                expectedItem = nep().pollNextExpectedCollectionItemInfo();
            }
            if (expectedItem == null) {
                incSkipLevel();
                return;
            }
            getCtx().pushNode(createInstance(expectedItem.getClazz()), curField(), null);
            nep().setExpectedCollectionItems(expectedItem.getCollectionItemsInfos());
        } else {
            Class clazz = ClassUtils.classForNameUnsafely(className);
            ObjectInfo expectedItem = expectedItem();
            if (expectedItem != null && !expectedItem.getClazz().isAssignableFrom(clazz)) {
                throw new IllegalStateException("Type mismatch");
            }
            getCtx().pushNode(clazz, curField(), null);
        }
    }

    private Object createInstance(Class aClass) {
        if (aClass == null) {
            throw new RuntimeException("No object expected");
        }
        return ClassUtils.createUnsafely(aClass);
    }

    public void finishObject(int ln, int col) {
        if (skipLevel > 0) {
            skipLevel--;
            ParsingNodeParams nep = nep();
            if (nep != null) {
                nep.setLastValue(IGNORE);
            }
            return;
        }

        Object o = getObj();
        getCtx().popNode();
        nep().setLastValue(o);
        out("finishObject");
    }

    public void startPair(int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        out("startPair");
    }

    public void keyPair(int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        String fieldName = nep().pollLastStringValue();
        FieldReflection f = getReflection().getField(fieldName);
        nep().setCurField(f);
        if (f != null) {
            if (f.getFieldKind() == FieldReflection.Kind.REFERENCE) {
                setExpectedItemAsSimpleClass(f.getFieldType());
            } else {
                setExpectedItem(null);
            }
        }
        out("keyPair");
    }

    public void finishPair(int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        ParsingNodeParams nep = nep();
        if (nep == null) {
            return;
        }
        FieldReflection f = nep.getCurField();
        Object o = nep.pollLastValue();
        if (o == NULL) {
            o = null;
        }
        if (f != null) {
            if (f.getFieldKind() == FieldReflection.Kind.COLLECTION) {
                Collection c = (Collection) o;
                Collection fc = (Collection) f.getValue(getObj());
                if (c == fc) {
                    //do nothing, the same collection
                } else if (f.getFieldType().isAssignableFrom(c.getClass())) {
                    f.setValue(getObj(), c);
                } else {
                    if (fc != null) {
                        fc.clear();
                        fc.addAll(c);
                    } else {
                        fc = (Collection) ClassUtils.createUnsafely(f.getFieldType());
                        fc.addAll(c);
                    }
                }
            } else if (o == null){
                f.setValue(getObj(), null);
            } else if (Timestamp.class.isAssignableFrom(f.getFieldType())) {
                f.setValue(getObj(), new Timestamp((Long) o));
            } else if (java.sql.Date.class.isAssignableFrom(f.getFieldType())) {
                f.setValue(getObj(), new java.sql.Date((Long) o));
            } else if (java.util.Date.class.isAssignableFrom(f.getFieldType())) {
                f.setValue(getObj(), new java.util.Date((Long) o));
            } else if (f.getFieldName().equals("id")) {
                f.setValue(getObj(), o);
            } else {
                if (f.getFieldType().isPrimitive()) {
                    //hopefully unboxing will work properly, else try to go with parsing:
                    try {
                        f.setValue(getObj(), o);
                    } catch (Exception e) {
                        Object parsedValue = ParseUtils.parseUnsafely(o.toString(), f.getFieldType());
                        f.setValue(getObj(), parsedValue);
                    }
                } else if (f.getFieldType().isAssignableFrom(o.getClass())) {
                    f.setValue(getObj(), o);
                } else {
                    Object parsedValue = ParseUtils.parseSafely(o.toString(), f.getFieldType());
                    f.setValue(getObj(), parsedValue);
                }
            }
            nep.setCurField(null);
        }
        out("finishPair");
    }

    public void startArray(int ln, int col) {
        if (skipLevel > 0) {
            incSkipLevel();
            return;
        }
        Collection c;
        FieldReflection f = curField();
        if (f != null) {
            if (f.getFieldKind() == FieldReflection.Kind.COLLECTION) {
                c = createCollectionForField(f);
                getCtx().pushNode(c, f, null);
                setExpectedItemAsSimpleClass(f.getCollectionItemClass());
            } else {
                incSkipLevel();
            }
        } else {
            ObjectInfo expectedItem = expectedItem();
            if (expectedItem == null) {
                expectedItem = nep().pollNextExpectedCollectionItemInfo();
            }
            if (expectedItem != null) {
                if (!expectedItem.isCollection()) {
                    incSkipLevel();
                    return;
                }
                c = (Collection) createInstance(expectedItem.getClazz());
                getCtx().pushNode(c, null, null);
                if (expectedItem.getCollectionItemInfo() != null) {
                    nep().setNextExpectedItem(expectedItem.getCollectionItemInfo());
                } else if (expectedItem.getCollectionItemsInfos() != null) {
                    nep().setExpectedCollectionItems(expectedItem.getCollectionItemsInfos());
                }
            }
        }
        out("startArray");
    }

    private void incSkipLevel() {
        skipLevel++;
    }

    private Collection createCollectionForField(FieldReflection f) {
        Collection c;
        c = (Collection) f.getValue(getObj());
        if (c == null) {
            c = (Collection) ClassUtils.createUnsafely(f.getFieldType());
        }
        return c;
    }

    public void finishArray(int ln, int col) {
        if (skipLevel > 0) {
            skipLevel--;
            return;
        }
        Collection c = (Collection) getObj();
        List<? extends ObjectInfo> eci = nep().getExpectedCollectionItems();
        if (strictExpectedObjects && eci != null && eci.size() != c.size()) {
            throw new RuntimeException("Not all expected objects parsed " + expectedTopLevelItems.size() + " != " + parsedTopLevelObjects.size());
        }
        getCtx().popNode();
        ParsingNodeParams nep = nep();
        if (nep != null) {
            nep.setLastValue(c);
        }
        out("finishArray");
    }

    public void valueInteger(String img, int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        Long val = Long.valueOf(img);

        if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
            nep().setLastValue(val);
        } else {
            Class expectedClass = null;
            if (curField() != null) {
                expectedClass = curField().getFieldType();
            }

            if (expectedClass != null) {
                if (Long.class.equals(expectedClass) || long.class.equals(expectedClass)) {
                    nep().setLastValue(val);
                } else if (Integer.class.equals(expectedClass) || int.class.equals(expectedClass)) {
                    nep().setLastValue(val.intValue());
                } else if (Short.class.equals(expectedClass) || short.class.equals(expectedClass)) {
                    nep().setLastValue(val.shortValue());
                } else if (Byte.class.equals(val.shortValue()) || byte.class.equals(expectedClass) ) {
                    nep().setLastValue(val.byteValue());
                } else {
                    nep().setLastValue(val.longValue());
                }
            } else {
                //Though it is risky, lets make an assumption it is an integer... (or may be better Long?)
                nep().setLastValue(val.longValue());
            }
        }
        out("valueInteger " + img);
    }

    public void valueDecimal(String img, int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        nep().setLastValue(Double.valueOf(img));
        out("valueDecimal");
    }

    public void valueDouble(String img, int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        nep().setLastValue(Double.valueOf(img));
        out("valueDouble");
    }

    public void valueString(String val, int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        val = unquote(val);
        nep().setLastStringValue(val);
        out("valueString " + val);
    }

    public void valueBoolean(boolean val, int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        nep().setLastValue(val);
        out("valueBoolean");
    }

    public void valueNull(int ln, int col) {
        if (skipLevel > 0) {
            return;
        }
        nep().setLastStringValue(null);
        nep().setLastValue(NULL);
        out("valueNull");
    }

    public void element(long ln, long col) {
        if (skipLevel > 0) {
            return;
        }
        Object element = nep().pollLastValue();
        Collection c = (Collection) getObj();
        if (element == NULL) {
            c.add(null);
            out("null element added");
        } else if (element == IGNORE) {
            if (nep().getExpectedCollectionItems() != null ) {
                throw new IllegalStateException("Expected type mismatch");
            }
            return;
        } else {
            ObjectInfo expectedItem = expectedItem();
            if (expectedItem == null) {
                expectedItem = nep().pollNextExpectedCollectionItemInfo();
            }
            if (expectedItem != null && element.getClass() != expectedItem.getClazz() &&
                    !expectedItem.getClazz().isAssignableFrom(element.getClass())) {
                element = CastUtils.castUnsafely(element, expectedItem.getClazz());
            }
            c.add(element);
            out("element added");
        }
    }

    private String unquote(String val) {
        if (val == null) {
            return null;
        }
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
            val = StringEscapeUtils.unescapeJavaScript(val);
        }
        return val;
    }

    protected FieldReflection curField() {
        ParsingNodeParams nep = nep();
        return nep == null ? null : nep.getCurField();
    }

    protected ObjectInfo expectedItem() {
        ParsingNodeParams nep = nep();
        return nep == null ? null : nep.getNextExpectedItem();
    }

    protected void setExpectedItemAsSimpleClass(Class clazz) {
        setExpectedItem(new ObjectInfoImpl(clazz));
    }

    protected void setExpectedItem(ObjectInfo clazz) {
        ParsingNodeParams nep = nep();
        if (nep != null) {
            nep.setNextExpectedItem(clazz);
        }
    }

    private void out(String val) {
//        System.out.println(val);
    }

}
