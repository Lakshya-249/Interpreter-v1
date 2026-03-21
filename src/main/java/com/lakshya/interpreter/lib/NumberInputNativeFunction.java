package com.lakshya.interpreter.lib;

import com.lakshya.interpreter.callable.Callable;
import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;
import java.util.Scanner;

public class NumberInputNativeFunction implements Callable {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return scanner.nextDouble();
    }
}
