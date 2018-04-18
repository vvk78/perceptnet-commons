package com.perceptnet.commons.utils;


public class Joiner {
    private String coma;
    private boolean skipNulls;
    private boolean skipEmpty;

    private Joiner(String coma) {
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

    public Joiner setComa(String coma) {
        this.coma = coma;
        return this;
    }

    public String join(Iterable<Object> items) {
        Iterable iterable;
        StringBuilder buff = new StringBuilder();
        for (Object item : items) {
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

    public String join(Object ... items) {
        return join(new ArrayIterable<>(items));
    }
}
