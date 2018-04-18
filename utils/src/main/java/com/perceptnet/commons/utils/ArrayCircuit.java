package com.perceptnet.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * This class is similar to
 *
 * EvictingQueue in Google Guava
 * CircularFifoQueue in Apache Commons
 *
 * This implementation is based on array
 *
 * Created by vkorovkin on 12.01.2017.
 */
public class ArrayCircuit<E> implements Collection<E> {
    private int _version = Integer.MIN_VALUE;
    private final int maxCapacity;
    private int shift;
    private final ArrayList<E> items;

    public ArrayCircuit(int capacity) {
        this(capacity, capacity);
    }

    public ArrayCircuit(int maxCapacity, int initialCapacity) {
        assertArgumentsOk(maxCapacity, initialCapacity);
        this.maxCapacity = maxCapacity;
        this.items = new ArrayList<E>(initialCapacity);
    }

    private void assertArgumentsOk(int maxCapacity, int initialCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity < 0");
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException("initialCapacity > maxCapacity");
        }
    }

    public E get(int i) {
        return items.get(translateIndex(i));
    }

    public E set(int i, E item) {
        return items.set(translateIndex(i), item);
    }

    private int translateIndex(int i) {
        if (shift <= 0) {
            return i;
        }
        int result = shift + i;
        int size = size();
        if (result >= size) {
            result = result - size;
        }
        return result;
    }

    @Override
    public boolean add(E e) {
        final int size = size();
        if (size == maxCapacity) {
            incShift();
            int idx = translateIndex(size - 1);
            items.set(idx, e);
            _version++;
            return true;
        } else {
            if (shift > 0) {
                int idx = translateIndex(size);
                items.add(idx, e);
                incShift();
                return true;
            } else {
                return items.add(e);
            }
        }
    }

    private void incShift() {
        shift++;
        if (shift == size()) {
            shift = 0;
        }
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new IterImpl();
    }

    @Override
    public Object[] toArray() {
//        Object[] rawResult = items.toArray();
//        if (shift)
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            E item = get(i);
            if (item.equals(o)) {
                int idx;
                if (shift > 0) {
                    idx = translateIndex(i);
                    if (idx <= i) {
                        shift--;
                    }
                } else {
                    idx = i;
                }
                items.remove(idx);
                _version++;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E e : c) {
            if (add(e)) {
                result = true;
            };
        }
        if (result) {
            _version++;
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public void clear() {
        items.clear();
        items.trimToSize();
        items.ensureCapacity(10);
        shift = 0;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //                                               I N N E R    C L A S S E S
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private final class IterImpl implements Iterator<E> {
        private final int versionAtConstruction = _version;
        private int idx;

        @Override
        public boolean hasNext() {
            assertNoModification();
            return idx < size();
        }

        @Override
        public E next() {
            assertNoModification();
            return get(idx++);
        }

        @Override
        public void remove() {

        }

        private void assertNoModification() {
            if (_version > versionAtConstruction) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
