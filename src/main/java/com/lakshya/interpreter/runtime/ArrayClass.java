package com.lakshya.interpreter.runtime;

import java.util.List;

public class ArrayClass {

    final List<Object> elements;

    public ArrayClass(List<Object> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
