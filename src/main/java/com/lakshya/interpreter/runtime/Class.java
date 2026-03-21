package com.lakshya.interpreter.runtime;

import java.util.List;
import java.util.Map;

public class Class implements Callable {

    public final String name;
    private final Map<String, Function> methods;

    public Class(String name, Map<String, Function> methods) {
        this.name = name;
        this.methods = methods;
    }

    public Function findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    public int arity() {
        Function initializer = findMethod("init");
        if (initializer != null) {
            return initializer.arity();
        }
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Instance instance = new Instance(this);
        Function initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public String toString() {
        return name;
    }
}
