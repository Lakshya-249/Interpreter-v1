package com.lakshya.interpreter.runtime;

public class Class {

    public final String name;

    public Class(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
