package com.perceptnet.commons.utils;

import com.perceptnet.abstractions.Adaptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import static com.perceptnet.commons.utils.RegexUtils.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 09.02.2018
 */
public class IncExlRegexFilter {
    public enum Order {IncludeThenExclude, ExcludeThenInclude}

    private boolean includeNulls;
    private Adaptor itemAdaptor;
    private Order order = Order.IncludeThenExclude;
    private Collection<Pattern> incFilters;
    private Collection<Pattern> exlFilters;

    public IncExlRegexFilter setIncludeNulls(boolean includeNulls) {
        this.includeNulls = includeNulls;
        return this;
    }

    public IncExlRegexFilter setItemAdaptor(Adaptor itemAdaptor) {
        this.itemAdaptor = itemAdaptor;
        return this;
    }

    public IncExlRegexFilter setOrder(Order order) {
        this.order = order;
        return this;
    }

    public IncExlRegexFilter setIncFilters(Collection<Pattern> incFilters) {
        this.incFilters = incFilters;
        return this;
    }

    public IncExlRegexFilter setExlFilters(Collection<Pattern> exlFilters) {
        this.exlFilters = exlFilters;
        return this;
    }

    public IncExlRegexFilter setIncFiltersStr(Collection<String> strings) {
        setIncFilters(patterns(strings));
        return this;
    }

    public IncExlRegexFilter setExlFiltersStr(Collection<String> strings) {
        setExlFilters(patterns(strings));
        return this;
    }

    public IncExlRegexFilter setIncFiltersSimpleWildcards(Collection<String> strings) {
        setIncFilters(simpleWildcardPatterns(strings));
        return this;
    }

    public IncExlRegexFilter setExlFiltersSimpleWildcards(Collection<String> strings) {
        setExlFilters(simpleWildcardPatterns(strings));
        return this;
    }


    public boolean isIncluded(Object o) {
        String s = itemStr(o);
        if (s == null) {
            return includeNulls;
        }
        Order order = this.order != null ? this.order : Order.IncludeThenExclude;
        if (order == Order.IncludeThenExclude) {
            outer:
                if (incFilters != null && !incFilters.isEmpty()) {
                    for (Pattern f : incFilters) {
                        if (f.matcher(s).matches()) {
                            break outer;
                        }
                    }
                    return false;
                }
            if (exlFilters != null && !exlFilters.isEmpty()) {
                for (Pattern f : exlFilters) {
                    if (f.matcher(s).matches()) {
                        return false;
                    }
                }
            }
            return true;
        } else if (order == Order.ExcludeThenInclude) {
            if (exlFilters != null && !exlFilters.isEmpty()) {
                for (Pattern f : exlFilters) {
                    if (f.matcher(s).matches()) {
                        return false;
                    }
                }
            }
            outer:
                if (incFilters != null && !incFilters.isEmpty()) {
                    for (Pattern f : incFilters) {
                        if (f.matcher(s).matches()) {
                            break outer;
                        }
                    }
                    return false;
                }
            return true;
        } else {
            throw new UnsupportedOperationException("Cannot support filtering setOrder: " + order);
        }
    }

    public void removeNotIncluded(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            Object next = it.next();
            if (!isIncluded(next)) {
                it.remove();
            }
        }
    }

    public <DEST extends Collection> DEST addIncluded(Collection source, DEST dest) {
        if (dest == null) {
            throw new NullPointerException("Dest is null");
        }
        for (Object o : source) {
            if (isIncluded(o)) {
                dest.add(o);
            }
        }
        return dest;
    }

    private String itemStr(Object o) {
        if (o == null) {
            return null;
        }
        if (itemAdaptor != null) {
            o = itemAdaptor.adapt(o);
        }
        if (o == null) {
            return null;
        }
        return o.toString();
    }
}
