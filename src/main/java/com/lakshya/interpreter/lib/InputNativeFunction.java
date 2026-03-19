package com.lakshya.interpreter.lib;

import com.lakshya.interpreter.runtime.Callable;
import com.lakshya.interpreter.runtime.Interpreter;
import java.util.List;
import java.util.Scanner;

public class InputNativeFunction implements Callable {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return scanner.nextLine();
    }

    @Override
    public String toString() {
        return "<native fn input>";
    }
}
