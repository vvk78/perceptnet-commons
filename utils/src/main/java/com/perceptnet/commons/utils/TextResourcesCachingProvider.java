package com.perceptnet.commons.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.perceptnet.commons.utils.MiscUtils.putIfAbsent;


/**
 * Utility class to work with text fragments in text resources. Text resources is considered to be split into pieces divided by a separator
 * always placed on a new single line. Separator by default starts with '---' prefix followed by name, but this is configurable. After a
 * separator a next text fragment goes down to a next fragment separator.
 *
 * Designed to be used in tests.
 *
 * Created by vkorovkin on 17.07.2017.
 */
public class TextResourcesCachingProvider {
    private final ConcurrentHashMap<String, Map<String, String>> resourcesOnNames = new ConcurrentHashMap();
    private Pattern nextItemStart = Pattern.compile("---(.+)");

    public TextResourcesCachingProvider() {
    }

    public TextResourcesCachingProvider setItemStartPattern(String pattern) {
        nextItemStart = Pattern.compile(pattern);
        return this;
    }

    public String get(String resourceName, String itemName) {
        Map<String, String> items = resourcesOnNames.get(resourceName);
        if (items == null) {
            items = putIfAbsent(resourcesOnNames, resourceName, loadTextItemsFromResource(resourceName, new HashMap<String, String>()));
        }
        return items.get(itemName);
    }

    private <M extends Map<String, String>> M loadTextItemsFromResource(String resourceName, M itemsMap) {
        InputStream is = null;
        BufferedReader r = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(resourceName);
            r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String curLine;
            String fragmentName = null;
            StringBuilder buff = null;
            while ((curLine = r.readLine()) != null) {
                Matcher m = nextItemStart.matcher(curLine);
                if (m.matches()) {
                    if (buff != null) {
                        String item = buff.toString();
                        itemsMap.put(fragmentName, item);
                    }
                    buff = new StringBuilder();
                    fragmentName = m.group(1).trim();
                } else if (buff != null) {
                    buff.append(curLine);
                    buff.append("\n");
                }
            }
            if (buff != null) {
                String query = buff.toString();
                itemsMap.put(fragmentName, query);
            }
            return itemsMap;
        } catch (Exception e) {
            throw new RuntimeException("Cannot load '" + resourceName + "' resource due to: " + e, e);
        } finally {
            IoUtils.closeSafely(r);
            IoUtils.closeSafely(is);
        }
    }



}
