package com.perceptnet.commons.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helps with java class imports. <b>It covers only trivial cases</b> Support for internal classes is not currently tested.
 * Aimed to be used in source generators and alike tools.
 *
 * created by vkorovkin on 18.06.2018
 */
public class ImportsHelper {
    private static final Set<String> PRIMITIVES = new HashSet<>(Arrays.asList(
            new String[]{"byte", "short", "int", "long", "float", "double", "char", "boolean", "void"}));

    private Map<String, String> imports = new HashMap<>(50);

    private Map<String, String> importsSwapped;

    /**
     * Adds full type name to imports and returns actual type name to be used in code (it may be short, or may be full if there are synonyms).
     */
    public String addImport(String qualifiedTypeName) {
        String simpleName = qualifiedTypeName.substring(qualifiedTypeName.lastIndexOf(".") + 1, qualifiedTypeName.length());
        if (qualifiedTypeName.startsWith("java.lang.") || PRIMITIVES.contains(qualifiedTypeName)) {
            return simpleName;
        }

        String result = imports.get(simpleName);
        if (result == null) {
            imports.put(simpleName, qualifiedTypeName);
            importsSwapped = null;
            return simpleName;
        } else {
            if (result.equals(qualifiedTypeName)) {
                return simpleName;
            } else {
                imports.put(qualifiedTypeName, qualifiedTypeName);
                importsSwapped = null;
                return qualifiedTypeName;
            }
        }
    }

    public Map<String, String> getImports() {
        return imports;
    }

    /**
     * Returns actual name to be used in java source code. It may be qualifiedName if class has not be imported
     * with {@link #addImport(String)} or there are name collisions. Or may be short if imports allow that.
     */
    public String actualName(String qualifiedName) {
        if (importsSwapped == null) {
            importsSwapped = MapUtils.changeKeyAndValues(imports);
        }
        String result = importsSwapped.get(qualifiedName);
        if (result == null) {
            return qualifiedName;
        }
        return result;
    }
}
