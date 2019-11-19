package com.perceptnet.commons.json;

import com.perceptnet.commons.json.formatting.SimpleJsonFormatter;
import com.perceptnet.commons.reflection.ReflectionProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 01.02.2018
 */
public class SimpleJsonUtils {
    public static String toJson(ReflectionProvider rp, Object o) {
        SimpleJsonFormatter f = new SimpleJsonFormatter(rp);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, "UTF-8");
            f.setOut(ps);
            f.process(o);
            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            ps.close();
            return content;
        } catch (IOException e) {
            throw new RuntimeException("" + e, e);
        }
    }

//    public static Object fromJson(ReflectionProvider rp, String json, ObjectInfo... expected) {
//         p = new JsonVoSupportParser(new StringReader(json));
//        p.setReflectionProvider(rp);
//        if (expected != null && expected.length != 0) {
//            p.setExpectedTopLevelItems(Arrays.asList(expected));
//        }
//        try {
//            p.any();
//            return p.getParsedTopLevelObjects().get(0);
//        } catch (ParseException e) {
//            throw new RuntimeException("Cannot parse response due to " + e, e);
//        }
//    }

}
