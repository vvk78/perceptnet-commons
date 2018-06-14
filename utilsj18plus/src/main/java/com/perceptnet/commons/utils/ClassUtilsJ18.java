package com.perceptnet.commons.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * created by vkorovkin on 14.06.2018
 */
public class ClassUtilsJ18 {

    public static <T> String mostGeneralPackage(Collection<T> classInfos, Function<? super T, String> classNameSupplier) {
        List<String> items = null;
        for (T ci : classInfos) {
            String classQulifiedName = classNameSupplier.apply(ci);
            String[] serviceItems = classQulifiedName.substring(0, Math.max(0, classQulifiedName.lastIndexOf("."))).split("\\.");
            if (items == null) {
                items = Arrays.asList(serviceItems);
                } else {
                    int j = 0;
                    for (int i = 0; i < Math.min(items.size(), serviceItems.length); i++) {
                        if (items.get(i).equals(serviceItems[i])) {
                            j = i;
                        } else {
                            break;
                        }
                    }
                    if (j == 0) {
                        return "";
                    }
                    items = items.subList(0, j + 1);
                }
            }
            if (items == null) {
                return "";
            }
            return Joiner.on(".").join(items);
        }
}
