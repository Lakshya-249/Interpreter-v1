package com.lakshya.interpreter.runtime;

import java.util.List;

public class Class implements Callable {

    public final String name;

    public Class(String name) {
        this.name = name;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Instance instance = new Instance(this);
        return instance;
    }

    @Override
    public String toString() {
        return name;
    }
}
