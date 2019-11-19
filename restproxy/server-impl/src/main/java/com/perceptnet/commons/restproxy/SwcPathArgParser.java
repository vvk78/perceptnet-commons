package com.perceptnet.commons.restproxy;


import com.perceptnet.commons.utils.SimpleTypeInfo;
import com.perceptnet.commons.utils.ClassUtils;
import com.perceptnet.commons.utils.ParseUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Collection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 29.01.2018
 */
class SwcPathArgParser {
    Object parsePathItem(SimpleTypeInfo pd, String pathItem) throws SwcArgParsingException {
        if (pathItem == null || pathItem.equals("null")) {
            return null;
        }
        if (pd.isCollection()) {
            return parseCollectionArg(pd, pathItem);
        } else {
            Class clazz = pd.getClazz();
            if (clazz == String.class) {
                if (pathItem.equals("'null'")) {
                    return "null";
                }
                return StringEscapeUtils.unescapeJson(pathItem);
            } else {
                if (pathItem.isEmpty()) {
                    return null;
                }
                return ParseUtils.parseUnsafely(pathItem, clazz);
            }
        }
    }

    Collection parseCollectionArg(SimpleTypeInfo pd, String pathItem) throws SwcArgParsingException {
        SimpleTypeInfo ciInfo = pd.getCollectionItemInfo();
        if (ciInfo.getClazz() != String.class || !ciInfo.isFlat()) {
            throw new IllegalStateException("Cannot parse " + pd + " as path arg");
        }
        Collection result = (Collection) ClassUtils.createUnsafely(pd.getClazz());
        if (pathItem.isEmpty()) {
            return result;
        }

        String[] items = pathItem.split(",");
        for (String item : items) {
            result.add(ParseUtils.parseUnsafely(item, ciInfo.getClazz()));
        }

        return null;
    }

}
