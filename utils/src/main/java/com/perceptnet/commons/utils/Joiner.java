package com.perceptnet.commons.utils;


import com.perceptnet.abstractions.Adaptor;

public class Joiner {
    private String coma;
    private boolean skipNulls;
    private boolean skipEmpty;
    private Adaptor itemAdaptor;

    protected Joiner(String coma) {
        this.coma = coma;
    }

    public static Joiner on(String coma) {
        return new Joiner(coma);
    }

    public Joiner setSkipNulls(boolean skipNulls) {
        this.skipNulls = skipNulls;
        return this;
    }

    public Joiner setSkipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
        return this;
    }

    public Joiner adapt(Adaptor itemAdaptor) {
        this.itemAdaptor = itemAdaptor;
        return this;
    }

    public Joiner setComa(String coma) {
        this.coma = coma;
        return this;
    }

    public String join(Iterable items) {
        StringBuilder buff = new StringBuilder();
        for (Object item : items) {
            item = adaptItem(item);
            if (item == null && skipNulls) {
                continue;
            }
            String str = (item == null ? "null" : item.toString());
            if (str.isEmpty() && skipEmpty) {
                continue;
            }
            if (buff.length() > 0) {
                buff.append(coma);
            }
            buff.append(str);
        }
        return buff.toString();
    }

    public String joinArr(Object ... items) {
        return join(new ArrayIterable<>(items));
    }

    private Object adaptItem(Object o) {
        if (itemAdaptor == null || o == null) {
            return o;
        }
        return itemAdaptor.adapt(o);
    }
}
