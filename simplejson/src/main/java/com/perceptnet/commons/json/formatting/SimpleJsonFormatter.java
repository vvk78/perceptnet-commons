package com.perceptnet.commons.json.formatting;


import com.perceptnet.commons.beanprocessing.ProcessingContext;
import com.perceptnet.commons.beanprocessing.inspection.BeanInspector;
import com.perceptnet.commons.reflection.BeanReflection;
import com.perceptnet.commons.reflection.FieldReflection;
import com.perceptnet.commons.reflection.ReflectionProvider;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 26.12.2017
 */
public class SimpleJsonFormatter extends BeanInspector {

    private CharSequenceTranslator jsonEscape;


    private Printer p;

    public SimpleJsonFormatter(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, new Printer());
    }

    public SimpleJsonFormatter(ReflectionProvider reflectionProvider, Printer p) {
        super(new ProcessingContext(reflectionProvider, null));
        this.p = p;

        final Map<CharSequence, CharSequence> escapeJsonMap = new HashMap<>();
        escapeJsonMap.put("\"", "\\\"");
        escapeJsonMap.put("\\", "\\\\");
        escapeJsonMap.put("/", "\\/");
        this.jsonEscape = new AggregateTranslator(
                new LookupTranslator(Collections.unmodifiableMap(escapeJsonMap)),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)

                //special adjustment to avoid escaping cyrillic
//                ,JavaUnicodeEscaper.outsideOf(32, 0x7f)
        );
    }

    @Override
    public Object process(Object obj) {
        if (obj == null) {
            processValue(null, null, obj);
            return null;
        }
        try {
            if (obj instanceof Collection) {
                //Push root context
                getCtx().setRootNode(obj);
                processCollection(null, (Collection) obj);
                return obj;
            } else if (obj.getClass().isArray()) {
                //Push root context
                getCtx().setRootNode(obj);
                processArray(null, obj);
                return obj;
            } else {
                //Push root context
                getCtx().setRootNode(obj);
                doProcess();
                return obj;
            }
        } finally {
            if (getCtx().curNode() != null) {
                getCtx().popNode();
            }
        }
    }

    public SimpleJsonFormatter setOut(PrintStream out) {
        p.setOut(out);
        return this;
    }

    @Override
    protected void doProcess() {
        if (findProcessedObj(getObj()) != null && !isProcessVisited()) {
            if (log.isTraceEnabled()) {log.trace("Object {} is already processed, skipped", getObj());}
            return;
        }


        p.println("{");
        p.pushIndentation("  ");

        //To make sure id is always at head:
        final BeanReflection rfl = getReflection();
        FieldReflection idField = rfl.getIdField();
        boolean printComa = false;
        if (idField != null) {
            printEnquoted(idField.getFieldName());
            p.print(": ");
            processValue(idField, null, idField.getValue(getObj()));
            printComa = true;
        }

        for (Iterator<FieldReflection> it = rfl.getAllFields().iterator(); it.hasNext(); ) {
            FieldReflection f = it.next();
            if (f.getFieldKind() == FieldReflection.Kind.ID) {
                continue; //must be printed already
            }
            if (f.isWriteOnly()) {
                continue;
            }
            if (printComa) {
                p.println(",");
            }
            printEnquoted(f.getFieldName());
            p.print(": ");
            Object v = unpackValue(f.getValue(getObj()));
            processValue(f, null, v);
            printComa = true;
        }
        p.popIndentation();
        p.println();
        p.println("}");
    }

    private void processReference(FieldReflection f, Integer collectionIndex, Object v) {
        //Attention! Recursive processing:
        getCtx().pushNode(v, f, collectionIndex == null ? null : "" + collectionIndex);
        p.pushIndentation("  ");
        p.println();
        try {
            doProcess();
        } finally {
            getCtx().popNode();
            p.popIndentation();
        }
    }

    private void processValue(FieldReflection f, Integer collectionIndex, Object v) {
        if (v == null) {
            p.print("null");
        } else if (v instanceof Number || v instanceof Boolean) {
            p.print(v);
        } else if (v instanceof Enum) {
            printEnquoted(v);
        } else if (v instanceof Date) {
            p.print(((Date)v).getTime());
        } else if (v instanceof String) {
            printEnquoted(
                    jsonEscape.translate((String)v)
                    //StringEscapeUtils.escapeJson((String)v)
            );
        } else if (v instanceof Collection) {
            processCollection(f, (Collection) v);
        } else if (v.getClass().isArray()) {
            processArray(f, v);
        } else if (v instanceof Map) {
            //just skip for now
        } else {
            processReference(f, collectionIndex, v);
            //todo basically there is no problem to support any object, add support if needed
            //throw new UnsupportedOperationException(v.getClass().getName() + " is not supported");
        }
    }

    private void processArray(FieldReflection f, Object v) {
        if (v.getClass().isPrimitive()) {
            throw new UnsupportedOperationException("Primitive arrays are not supported yet");
        }
        p.print("[");
        Object[] arr = (Object[]) v;
        int lastIndex = (arr.length - 1);
        for (int i = 0; i < arr.length; i++ ) {
            processValue(f, i, unpackValue(arr[i]));
            if ( i != lastIndex) {
                p.print(", ");
            }
        }
        p.print("]");
    }

    private void processCollection(FieldReflection f, Collection v) {
        p.print("[");
        int idx = 0;
        for (Iterator ci = v.iterator(); ci.hasNext(); ) {
            processValue(f, idx, unpackValue(ci.next()));
            if (ci.hasNext()) {
                p.print(", ");
            }
            idx++;
        }
        p.print("]");
    }

    private void printEnquoted(Object v) {
        p.print("\"");
        p.print(v);
        p.print("\"");
    }

    private void printlnEnquoted(Object v) {
        printEnquoted(v);
        p.println();
    }

    Object unpackValue(Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        return rawValue;
    }

}
