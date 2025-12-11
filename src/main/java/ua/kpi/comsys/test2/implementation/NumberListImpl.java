/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 *
 * @author Zernyshkin Illia Denisovich, IO-34, №3404
 *
 */
public class NumberListImpl implements NumberList {

    private static final int RECORD_BOOK_NUMBER = 3404;
    private static final int[] BASES = {2, 3, 8, 10, 16};

    private final int base;
    private Node head;
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.base = determineMainBase();
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this.base = determineMainBase();
        if (file == null || !file.exists()) {
            return;
        }
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8).trim();
            loadFromDecimalString(content);
        } catch (IOException e) {
            // leave list empty
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this.base = determineMainBase();
        loadFromDecimalString(value);
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            return;
        }
        try {
            Files.writeString(file.toPath(), toDecimalString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // ignore silently
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return RECORD_BOOK_NUMBER;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger value = toBigInteger();
        int targetBase = determineAdditionalBase();
        NumberListImpl result = new NumberListImpl(targetBase);
        result.populateFromBigInteger(value);
        return result;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) {
            return new NumberListImpl();
        }
        BigInteger left = toBigInteger();
        BigInteger right = toBigInteger(arg);
        BigInteger product = left.multiply(right);
        NumberListImpl result = new NumberListImpl(base);
        result.populateFromBigInteger(product);
        return result;
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        return toBigInteger().toString();
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        Node current = head;
        for (int i = 0; i < size; i++) {
            builder.append(digitToChar(current.value));
            current = current.next;
        }
        return builder.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NumberList)) {
            return false;
        }
        return toDecimalString().equals(toBigInteger((NumberList) o).toString());
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new ListItr(0);
    }


    @Override
    public Object[] toArray() {
        Byte[] arr = new Byte[size];
        int i = 0;
        for (Byte b : this) {
            arr[i++] = b;
        }
        return arr;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not implemented per assignment");
    }


    @Override
    public boolean add(Byte e) {
        if (!isValidDigit(e)) {
            return false;
        }
        linkLast(e);
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.value.equals(o)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            return false;
        }
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        return addAll(size, c);
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (c == null) {
            return false;
        }
        checkPositionIndex(index);
        int added = 0;
        Node successor = (index == size) ? null : nodeAt(index);
        for (Byte b : c) {
            if (!isValidDigit(b)) {
                continue;
            }
            linkBefore(b, successor);
            added++;
            if (successor == null && head != null) {
                successor = head;
            }
        }
        return added > 0;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null || isEmpty()) {
            return false;
        }
        boolean modified = false;
        Node current = head;
        int traversed = 0;
        while (traversed < size) {
            Node next = current.next;
            if (c.contains(current.value)) {
                unlink(current);
                modified = true;
            }
            current = next;
            traversed++;
            if (isEmpty()) {
                break;
            }
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            clear();
            return true;
        }
        boolean modified = false;
        Node current = head;
        int traversed = 0;
        while (traversed < size) {
            Node next = current.next;
            if (!c.contains(current.value)) {
                unlink(current);
                modified = true;
            }
            current = next;
            traversed++;
            if (isEmpty()) {
                break;
            }
        }
        return modified;
    }


    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        return nodeAt(index).value;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (!isValidDigit(element)) {
            throw new IllegalArgumentException("Invalid digit for base " + base);
        }
        Node node = nodeAt(index);
        Byte old = node.value;
        node.value = element;
        return old;
    }


    @Override
    public void add(int index, Byte element) {
        if (!isValidDigit(element)) {
            throw new IllegalArgumentException("Invalid digit for base " + base);
        }
        checkPositionIndex(index);
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, nodeAt(index));
        }
    }


    @Override
    public Byte remove(int index) {
        Node node = nodeAt(index);
        Byte old = node.value;
        unlink(node);
        return old;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.value.equals(o)) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        Node current = head == null ? null : head.prev;
        for (int i = size - 1; i >= 0; i--) {
            if (current != null && current.value.equals(o)) {
                return i;
            }
            current = current == null ? null : current.prev;
        }
        return -1;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        return new ListItr(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        DoublyLinkedListBuffer buffer = new DoublyLinkedListBuffer(base);
        Node node = (fromIndex == size) ? null : nodeAt(fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            buffer.add(node.value);
            node = node.next;
        }
        return buffer.toList();
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index2 < 0 || index1 >= size || index2 >= size) {
            return false;
        }
        if (index1 == index2) {
            return true;
        }
        Node n1 = nodeAt(index1);
        Node n2 = nodeAt(index2);
        Byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }


    @Override
    public void sortAscending() {
        sortInternal(true);
    }


    @Override
    public void sortDescending() {
        sortInternal(false);
    }


    @Override
    public void shiftLeft() {
        if (size > 1) {
            head = head.next;
        }
    }


    @Override
    public void shiftRight() {
        if (size > 1) {
            head = head.prev;
        }
    }

    // ====================== internal helpers ======================

    private NumberListImpl(int base) {
        this.base = base;
    }

    private void loadFromDecimalString(String value) {
        if (value == null) {
            return;
        }
        value = value.trim();
        if (value.isEmpty() || value.startsWith("-")) {
            return;
        }
        if (!value.chars().allMatch(Character::isDigit)) {
            return;
        }
        BigInteger big = new BigInteger(value);
        populateFromBigInteger(big);
    }

    private void populateFromBigInteger(BigInteger number) {
        clear();
        if (number == null) {
            return;
        }
        if (number.signum() == 0) {
            add((byte) 0);
            return;
        }
        BigInteger b = number;
        BigInteger baseBI = BigInteger.valueOf(base);
        ArrayList<Byte> digits = new ArrayList<>();
        while (b.signum() > 0) {
            BigInteger[] divRem = b.divideAndRemainder(baseBI);
            digits.add(divRem[1].byteValue());
            b = divRem[0];
        }
        Collections.reverse(digits);
        for (Byte d : digits) {
            add(d);
        }
    }

    private BigInteger toBigInteger() {
        return toBigInteger(this);
    }

    private BigInteger toBigInteger(NumberList list) {
        if (list == null || list.isEmpty()) {
            return BigInteger.ZERO;
        }
        int listBase = (list instanceof NumberListImpl) ? ((NumberListImpl) list).base : determineMainBase();
        BigInteger bBase = BigInteger.valueOf(listBase);
        BigInteger result = BigInteger.ZERO;
        for (Byte digit : list) {
            result = result.multiply(bBase).add(BigInteger.valueOf(digit));
        }
        return result;
    }

    private int determineMainBase() {
        int c5 = getRecordBookNumber() % 5;
        return BASES[c5];
    }

    private int determineAdditionalBase() {
        int c5 = getRecordBookNumber() % 5;
        int idx = (c5 + 1) % BASES.length;
        return BASES[idx];
    }

    private boolean isValidDigit(Byte b) {
        return b != null && b >= 0 && b < base;
    }

    private void linkLast(Byte value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
    }

    private void linkBefore(Byte value, Node successor) {
        if (successor == null) {
            linkLast(value);
            return;
        }
        Node newNode = new Node(value);
        Node predecessor = successor.prev;
        newNode.next = successor;
        newNode.prev = predecessor;
        successor.prev = newNode;
        if (predecessor != null) {
            predecessor.next = newNode;
        }
        if (successor == head) {
            head = newNode;
        }
        size++;
    }

    private void unlink(Node node) {
        if (size == 1) {
            head = null;
            size = 0;
            return;
        }
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
        if (node == head) {
            head = next;
        }
        size--;
    }

    private Node nodeAt(int index) {
        checkElementIndex(index);
        Node current;
        if (index < (size / 2)) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
        }
    }

    private char digitToChar(Byte digit) {
        if (digit < 10) {
            return (char) ('0' + digit);
        }
        return (char) ('A' + (digit - 10));
    }

    private void sortInternal(boolean asc) {
        if (size < 2) {
            return;
        }
        Byte[] arr = new Byte[size];
        int i = 0;
        for (Byte b : this) {
            arr[i++] = b;
        }
        Arrays.sort(arr, (a, b) -> asc ? Byte.compare(a, b) : Byte.compare(b, a));
        clear();
        for (Byte b : arr) {
            add(b);
        }
    }

    private static final class Node {
        private Byte value;
        private Node next;
        private Node prev;

        Node(Byte value) {
            this.value = value;
        }
    }

    private final class ListItr implements ListIterator<Byte> {
        private Node nextNode;
        private Node lastReturned;
        private int nextIndex;

        ListItr(int index) {
            if (index == size) {
                nextNode = null;
                nextIndex = size;
            } else {
                nextNode = nodeAt(index);
                nextIndex = index;
            }
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            lastReturned = nextNode;
            nextNode = nextNode.next;
            nextIndex++;
            return lastReturned.value;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public Byte previous() {
            if (!hasPrevious()) {
                throw new java.util.NoSuchElementException();
            }
            nextNode = (nextNode == null) ? head.prev : nextNode.prev;
            lastReturned = nextNode;
            nextIndex--;
            return lastReturned.value;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            Node lastNext = lastReturned.next;
            unlink(lastReturned);
            if (nextNode == lastReturned) {
                nextNode = lastNext;
            } else {
                nextIndex--;
            }
            lastReturned = null;
        }

        @Override
        public void set(Byte byteObj) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            if (!isValidDigit(byteObj)) {
                throw new IllegalArgumentException("Invalid digit for base " + base);
            }
            lastReturned.value = byteObj;
        }

        @Override
        public void add(Byte byteObj) {
            if (!isValidDigit(byteObj)) {
                throw new IllegalArgumentException("Invalid digit for base " + base);
            }
            if (nextNode == null) {
                linkLast(byteObj);
            } else {
                linkBefore(byteObj, nextNode);
            }
            nextIndex++;
            lastReturned = null;
        }
    }

    private static final class DoublyLinkedListBuffer {
        private final ArrayList<Byte> data = new ArrayList<>();
        private final int base;

        DoublyLinkedListBuffer(int base) {
            this.base = base;
        }

        void add(Byte b) {
            data.add(b);
        }

        List<Byte> toList() {
            NumberListImpl list = new NumberListImpl(base);
            for (Byte b : data) {
                list.add(b);
            }
            return list;
        }
    }
}
