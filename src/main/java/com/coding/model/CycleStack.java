package com.coding.model;

import java.util.ArrayList;
import java.util.List;

/**
 * stack with a loop limit array
 */
public class CycleStack {

    private final Long[] elements;
    private int head;
    private int size;

    public CycleStack(int capacity) {
        this.elements = new Long[capacity];
        this.head = 0;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == elements.length;
    }

    public List<Long> getData() {
        List<Long> dataList = new ArrayList<>(elements.length);
        for (Long element : elements) {
            if (element != null) {
                dataList.add(element);
            }
        }

        return dataList;
    }

    public void push(Long element) {
        if (isFull()) {
            elements[head] = null;
            size--;
        }
        elements[head] = element;
        head = (head + 1) % elements.length;
        size++;
    }

}
