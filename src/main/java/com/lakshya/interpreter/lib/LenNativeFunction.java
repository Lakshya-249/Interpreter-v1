package com.lakshya.interpreter.lib;

import com.lakshya.interpreter.callable.ArrayClass;
import com.lakshya.interpreter.callable.Callable;
import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;

public class LenNativeFunction implements Callable {

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof String) {
            return ((String) arg).length();
        }
        if (arg instanceof ArrayClass) {
            return ((ArrayClass) arg).size();
        }

        return null;
    }
}
