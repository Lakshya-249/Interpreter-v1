package com.lakshya.interpreter.lib;

import com.lakshya.interpreter.callable.Callable;
import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;

public class ClockNativeFunction implements Callable {

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    @Override
    public String toString() {
        return "<native fn>";
    }
}
