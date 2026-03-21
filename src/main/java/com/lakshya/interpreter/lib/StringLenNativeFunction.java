package com.lakshya.interpreter.lib;

import com.lakshya.interpreter.callable.Callable;
import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;

public class StringLenNativeFunction implements Callable {

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return arguments.get(0).toString().length();
    }
}
