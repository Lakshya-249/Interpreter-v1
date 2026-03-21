package com.lakshya.interpreter.callable;

import com.lakshya.interpreter.lexer.Token;
import com.lakshya.interpreter.runtime.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Instance {

    private final Class klass;

    private final Map<String, Object> fields = new HashMap<>();

    public Instance(Class klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return "<" + klass.name + " instance>";
    }

    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        Function method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(
            name,
            "Undefined property '" + name.lexeme + "'."
        );
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }
}
