package com.lakshya.interpreter.callable;

import com.lakshya.interpreter.lexer.Token;
import com.lakshya.interpreter.runtime.Interpreter;
import com.lakshya.interpreter.runtime.RuntimeError;
import java.util.List;
import java.util.Map;

public class Class implements Callable {

    public final String name;
    private final Map<String, Function> methods;
    private final Map<String, Function> staticMethods;

    public Class(
        String name,
        Map<String, Function> methods,
        Map<String, Function> staticMethods
    ) {
        this.name = name;
        this.methods = methods;
        this.staticMethods = staticMethods;
    }

    public Object get(Token name) {
        if (staticMethods.containsKey(name.lexeme)) {
            return staticMethods.get(name.lexeme);
        }

        throw new RuntimeError(
            name,
            "Undefined static method '" + name.lexeme + "'."
        );
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
