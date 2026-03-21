package com.lakshya.interpreter.callable;

import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;

public interface Callable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
