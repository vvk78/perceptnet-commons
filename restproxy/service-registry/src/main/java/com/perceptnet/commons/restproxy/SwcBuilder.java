package com.perceptnet.commons.restproxy;

import com.perceptnet.commons.utils.SimpleTypeInfo;
import com.perceptnet.commons.utils.SimpleTypeInfoImpl;
import com.perceptnet.commons.utils.ClassUtils;
import com.perceptnet.commons.utils.ResourceUtils;
import com.perceptnet.commons.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 25.01.2018
 */
public class SwcBuilder {
    private static final Set<Class> PRIMITIVE_CLASSES = new HashSet<Class>(Arrays.asList(
            byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, void.class));


    Set<Class> loadDeclaredServices(String serviceMappingResName) {
        List<String> mappings = ResourceUtils.getResourceLines(serviceMappingResName, true, true, "#", 100);
        Set<Class> result = new HashSet<>(mappings.size());
        for (String mapping : mappings) {
            String name = StringUtils.getHeadOrNull(mapping, "=");
            Class serviceClass = ClassUtils.classForNameUnsafely(name);
            result.add(serviceClass);
        }
        return result;
    }

    public Map<String, List<MethodDescription>> mapServiceMethods(Class serviceClass) {
        Method[] methods = serviceClass.getMethods();
        Map<String, List<MethodDescription>> map = new HashMap<>();
        for (Method method : methods) {
            if (method.getDeclaringClass() != serviceClass) {
                continue;
            }
            String mn = method.getName();
            List<MethodDescription> overloaded = map.get(mn);
            if (overloaded == null) {
                overloaded = new ArrayList<>(3);
                map.put(mn, overloaded);
            }
            buildMethodDescription(method);
            overloaded.add(buildMethodDescription(method));
        }
        return map;
    }

    MethodDescription buildMethodDescription(Method method) {
        List<SimpleTypeInfo> paramDescriptions = buildParamsList(method);
        SimpleTypeInfo resultInfo = buildObjectInfo(method.getGenericReturnType());
        return new MethodDescription(method, resultInfo,
                buildParamsList(method), isLastNotFlat(paramDescriptions), isAllArgsFromBody(paramDescriptions));
    }

    List<SimpleTypeInfo> buildParamsList(Method method) {
        Type[] types = method.getGenericParameterTypes();
        if (types == null || types.length == 0) {
            return Collections.emptyList();
        }
        List<SimpleTypeInfo> result = new ArrayList<>(types.length);
        for (Type type : types) {
            result.add(buildObjectInfo(type));
        }
        return result;
    }

    SimpleTypeInfo buildObjectInfo(Type type) {
        SimpleTypeInfoImpl result = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Class clazz = (Class) pt.getRawType();
            result = new SimpleTypeInfoImpl(clazz, pt.getActualTypeArguments());
            if (result.isCollection()) {
                result.setCollectionItemInfo(detectCollectionItemType(pt));
                if (result.getCollectionItemInfo() != null) {
                    result.setFlat(result.getCollectionItemInfo().isFlat());
                }
            } else {
                result.setFlat(isFlat(clazz));
            }
        } else if (type instanceof Class) {
            Class clazz = (Class) type;
            result = new SimpleTypeInfoImpl(clazz);
            result.setFlat(isFlat(clazz));
        } else {
            return new SimpleTypeInfoImpl(Object.class);
            //throw new IllegalStateException("Unknown type: " + type);
        }
        return result;
    }

    boolean isFlat(Class clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isEnum() || clazz == Boolean.class || clazz == Character.class || clazz == String.class || PRIMITIVE_CLASSES.contains(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz);
    }

    SimpleTypeInfo detectCollectionItemType(ParameterizedType parameterizedType) {
        SimpleTypeInfoImpl result;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) {
            return null;
        }

        Type t = types[0];
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            result = new SimpleTypeInfoImpl((Class) pt.getRawType(), pt.getActualTypeArguments());
        } else if (t instanceof Class) {
            result = new SimpleTypeInfoImpl((Class) t);
        } else {
            return null;
            //throw new IllegalStateException("Unknown type: " + t);
        }

        result.setFlat(isFlat(result.getCollectionItemInfo().getClazz()));
        return result;
    }

    boolean isAllArgsFromBody(List<SimpleTypeInfo> paramDescriptions) {
        if (paramDescriptions.isEmpty()) {
            return false;
        }
        int notFlatCount = 0;
        for (SimpleTypeInfo pd : paramDescriptions) {
            if (pd.isFlat()) {
                notFlatCount++;
            }
        }
        SimpleTypeInfo lastPd = paramDescriptions.get(paramDescriptions.size() - 1);
        return notFlatCount > 1 || (notFlatCount > 0 && !lastPd.isFlat());
    }

    boolean isLastNotFlat(List<SimpleTypeInfo> paramDescriptions) {
        return !paramDescriptions.isEmpty() && !paramDescriptions.get(paramDescriptions.size() - 1).isFlat();
    }




}
