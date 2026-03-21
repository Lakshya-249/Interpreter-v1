package com.lakshya.interpreter.callable;

import com.lakshya.interpreter.lexer.Token;
import com.lakshya.interpreter.runtime.Interpreter;
import com.lakshya.interpreter.runtime.RuntimeError;
import java.util.List;

public class ArrayClass {

    public final List<Object> elements;

    public ArrayClass(List<Object> elements) {
        this.elements = elements;
    }

    public int size() {
        return elements.size();
    }

    public Object get(Token name) {
        switch (name.lexeme) {
            case "push_back":
                return new Callable() {
                    @Override
                    public int arity() {
                        return 1;
                    }

                    @Override
                    public Object call(
                        Interpreter interpreter,
                        List<Object> args
                    ) {
                        elements.add(args.get(0));
                        return null;
                    }

                    @Override
                    public String toString() {
                        return "<native fn push_back>";
                    }
                };
            case "pop_back":
                return new Callable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(
                        Interpreter interpreter,
                        List<Object> args
                    ) {
                        if (elements.isEmpty()) {
                            throw new RuntimeError(
                                name,
                                "Cannot pop from an empty array."
                            );
                        }
                        return elements.remove(elements.size() - 1);
                    }

                    @Override
                    public String toString() {
                        return "<native fn pop_back>";
                    }
                };
        }
        throw new RuntimeError(
            name,
            "Undefined property '" + name.lexeme + "'."
        );
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
