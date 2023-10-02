package com.perceptnet.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by vkorovkin on 08.06.2018
 */
public class OptionUtils {
    public static Map<String, List<String>> parseOptions(String[] args) {
        Map<String, List<String>> result = new HashMap();
        List<String> optionArgs = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (result.containsKey(arg)) {
                    throw new IllegalArgumentException("Option " + arg + " is defined more than once");
                }
                optionArgs = new ArrayList(5);
                result.put(arg, optionArgs);
            } else {
                if (optionArgs == null) {
                    optionArgs = new ArrayList(5);
                    result.put(null, optionArgs);
                }
                optionArgs.add(arg);
            }
        }
        return result;
    }
}
