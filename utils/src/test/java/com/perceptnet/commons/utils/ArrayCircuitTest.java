package com.perceptnet.commons.utils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.testng.Assert.*;
import static com.perceptnet.commons.tests.TestGroups.*;

/**
 * Created by vkorovkin on 12.01.2017.
 */
public class ArrayCircuitTest {

    private ArrayCircuit<Integer> circuit;

    @BeforeMethod(groups = {UNIT, INTEGRATION, DEBUG, FUNCTIONAL, TEMPORARILY_DISABLED})
    public void beforeTestMethod() {
        circuit = new ArrayCircuit<>(5, 5);
    }


    @Test(groups = {UNIT}, expectedExceptions = {IllegalArgumentException.class})
    public void testConstructionArgumentsFailure1() throws Exception {
        new ArrayCircuit(-1, 0);
    }

    @Test(groups = {UNIT}, expectedExceptions = {IllegalArgumentException.class})
    public void testConstructionArgumentsFailure2() throws Exception {
        new ArrayCircuit(10, 11);
    }

    @Test(groups = {UNIT})
    public void testConstruction() throws Exception {
        new ArrayCircuit(10, 10);
        new ArrayCircuit(1, 0);
        new ArrayCircuit(1, 1);
        new ArrayCircuit(1);
    }

    @Test(groups = {UNIT})
    public void testAdditionAndSettings() throws Exception {
        circuit.add(1);
        assertEquals(circuit.size(), 1, "Wrong size");
        circuit.add(2);
        circuit.add(3);
        assertEquals(circuit.size(), 3, "Wrong size");
        circuit.add(4);
        circuit.add(5);
        assertEquals(circuit.size(), 5, "Wrong size");
        assertItems(1, 2, 3, 4, 5);

        circuit.add(23);
        assertEquals(circuit.size(), 5, "Wrong size");
        assertItems(2, 3, 4, 5, 23);

        circuit.add(8);
        assertEquals(circuit.size(), 5, "Wrong size");
        assertItems(3, 4, 5, 23, 8);

        circuit.set(0, 16);
        assertItems(16, 4, 5, 23, 8);

        circuit.set(4, 17);
        assertItems(16, 4, 5, 23, 17);

        circuit.set(2, 55);
        assertItems(16, 4, 55, 23, 17);
    }

    @Test(groups = {UNIT})
    public void testRemoval() throws Exception {
        addItems(1, 2, 3, 4, 5, 6, 7);
        assertItems(3, 4, 5, 6, 7);

        circuit.remove(5);
        assertItems(3, 4, 6, 7);

        addItems(8);
        assertItems(3, 4, 6, 7, 8);

        addItems(9);
        assertItems(4, 6, 7, 8, 9);

        circuit.remove(4);
        assertItems(6, 7, 8, 9);

        circuit.remove(7);
        assertItems(6, 8, 9);

        addItems(10);
        assertItems(6, 8, 9, 10);

        addItems(11);
        assertItems(6, 8, 9, 10, 11);

        circuit.remove(11);
        assertItems(6, 8, 9, 10);

        addItems(11);
        assertItems(6, 8, 9, 10, 11);

        addItems(12);
        assertItems(8, 9, 10, 11, 12);

        addItems(20, 21, 22, 23, 24);
        assertItems(20, 21, 22, 23, 24);

        circuit.remove(20);
        assertItems(21, 22, 23, 24);

        circuit.remove(23);
        assertItems(21, 22, 24);

        circuit.remove(24);
        assertItems(21, 22);

        circuit.remove(22);
        assertItems(21);

        circuit.remove(21);
        assertEquals(circuit.size(), 0, "Wrong size");

        addItems(30, 31, 32, 33, 34, 35);
        assertItems(31, 32, 33, 34, 35);

        circuit.clear();
        assertEquals(circuit.size(), 0, "Wrong size");

        addItems(30, 31, 32, 33, 34, 35, 36, 37);
        assertItems(33, 34, 35, 36, 37);

        circuit.clear();
        assertEquals(circuit.size(), 0, "Wrong size");
    }

    @Test(groups = {UNIT})
    public void testIteration() throws Exception {
        Iterator<Integer> it = circuit.iterator();
        assertFalse(it.hasNext(), "Wrrong hash next");
        addItems(1, 2, 3, 4, 5, 6, 7);

        it = circuit.iterator();
        assertTrue(it.hasNext(), "Wrong hasNext");

        assertEquals((int)it.next(), 3, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 4, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 5, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 6, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 7, "Wrong iteration");
        assertFalse(it.hasNext(), "Wrong hasNext");
    }

    @Test(groups = {UNIT}, expectedExceptions = {ConcurrentModificationException.class})
    public void testConcurrentModificationOnAddition() throws Exception {
        addItems(1, 2, 3, 4, 5, 6, 7);

        Iterator<Integer> it = circuit.iterator();
        assertTrue(it.hasNext(), "Wrong hasNext");

        assertEquals((int)it.next(), 3, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 4, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");

        addItems(13);
        it.next();
    }

    @Test(groups = {UNIT}, expectedExceptions = {ConcurrentModificationException.class})
    public void testConcurrentModificationOnRemoval() throws Exception {
        addItems(1, 2, 3, 4, 5, 6, 7);

        Iterator<Integer> it = circuit.iterator();
        assertTrue(it.hasNext(), "Wrong hasNext");

        assertEquals((int)it.next(), 3, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");
        assertEquals((int)it.next(), 4, "Wrong iteration");
        assertTrue(it.hasNext(), "Wrong hasNext");

        circuit.remove(7);
        it.next();
    }

    private void addItems(int ... items) {
        for (int item : items) {
            circuit.add(item);
        }
    }

    private void assertItems(int ... items) {
        assertEquals(items.length, circuit.size(), "Wrong items size");
        int i = 0;
        for (int item : items) {
            assertEquals((int)circuit.get(i), item, "Wrong item at idx " + i);
            i++;
        }
    }

}
