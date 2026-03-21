package com.lakshya.interpreter.runtime;

import com.lakshya.interpreter.App;
import com.lakshya.interpreter.ast.Expr;
import com.lakshya.interpreter.ast.Stmt;
import com.lakshya.interpreter.callable.ArrayClass;
import com.lakshya.interpreter.callable.Callable;
import com.lakshya.interpreter.callable.Class;
import com.lakshya.interpreter.callable.Function;
import com.lakshya.interpreter.callable.Instance;
import com.lakshya.interpreter.lexer.Token;
import com.lakshya.interpreter.lexer.TokenType;
import com.lakshya.interpreter.lib.ClockNativeFunction;
import com.lakshya.interpreter.lib.InputNativeFunction;
import com.lakshya.interpreter.lib.NumberInputNativeFunction;
import com.lakshya.interpreter.lib.StringLenNativeFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter() {
        globals.define("clock", new ClockNativeFunction());
        globals.define("input", new InputNativeFunction());
        globals.define("numberInput", new NumberInputNativeFunction());
        globals.define("len", new StringLenNativeFunction());
    }

    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    public Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        }

        return globals.get(name);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private void checkNumberOperands(
        Token operator,
        Object left,
        Object right
    ) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void divisionByZero(Token operator, Object right) {
        if ((double) right == 0) throw new RuntimeError(
            operator,
            "Division by zero"
        );
    }

    public void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GREATER_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_THAN_EQUALS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS_THAN:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_THAN_EQUALS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case NOT_EQUALS:
                return !isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                divisionByZero(expr.operator, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                } else if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                );
            default:
                break;
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double) right;
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitFunctionExpr(Expr.Function expr) {
        Stmt.Function function = new Stmt.Function(
            null,
            expr.params,
            expr.body
        );
        return new Function(function, environment, false);
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable)) {
            throw new RuntimeError(
                expr.paren,
                "Can only call functions and classes."
            );
        }

        Callable function = (Callable) callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(
                expr.paren,
                "Expected " +
                    function.arity() +
                    " arguments but got " +
                    arguments.size() +
                    "."
            );
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof Instance) {
            return ((Instance) object).get(expr.name);
        }

        if (object instanceof ArrayClass) {
            return ((ArrayClass) object).get(expr.name);
        }

        if (object instanceof Class) {
            return ((Class) object).get(expr.name);
        }

        throw new RuntimeError(
            expr.name,
            "Undefined property '" + expr.name.lexeme + "'."
        );
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);

        if (!(object instanceof Instance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((Instance) object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitArrayExpr(Expr.Array expr) {
        List<Object> values = new ArrayList<>();
        for (Expr element : expr.elements) {
            values.add(evaluate(element));
        }
        return new ArrayClass(values);
    }

    @Override
    public Object visitIndexExpr(Expr.Index expr) {
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);

        if (!(array instanceof ArrayClass)) {
            throw new RuntimeError(expr.paren, "Only arrays have indices.");
        }

        if (!(index instanceof Double)) {
            throw new RuntimeError(expr.paren, "Array indices must be number.");
        }

        int idx = (int) ((double) index);

        List<Object> values = ((ArrayClass) array).elements;
        if (idx < 0 || idx >= values.size()) {
            throw new RuntimeError(
                expr.paren,
                "Array index out of bounds: " +
                    idx +
                    " (size: " +
                    values.size() +
                    ")"
            );
        }

        return values.get(idx);
    }

    @Override
    public Object visitSetIndexExpr(Expr.SetIndex expr) {
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);
        Object value = evaluate(expr.value);

        if (!(array instanceof ArrayClass)) {
            throw new RuntimeError(expr.paren, "Only arrays have indices.");
        }

        if (!(index instanceof Double)) {
            throw new RuntimeError(expr.paren, "Array indices must be number.");
        }

        int idx = (int) ((double) index);

        List<Object> elements = ((ArrayClass) array).elements;
        if (idx < 0 || idx >= elements.size()) {
            throw new RuntimeError(
                expr.paren,
                "Array index out of bounds: " +
                    idx +
                    " (size: " +
                    elements.size() +
                    ")"
            );
        }

        elements.set(idx, value);

        return value;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        Function function = new Function(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;

        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }

        throw new Return(value);
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        environment.define(stmt.name.lexeme, null);

        Map<String, Function> methods = new HashMap<>();
        Map<String, Function> staticMethods = new HashMap<>();

        for (Stmt.Function method : stmt.staticMethods) {
            Function function = new Function(method, environment, false);
            staticMethods.put(method.name.lexeme, function);
        }

        for (Stmt.Function method : stmt.methods) {
            Function function = new Function(
                method,
                environment,
                method.name.lexeme.equals("init")
            );
            methods.put(method.name.lexeme, function);
        }

        Class klass = new Class(stmt.name.lexeme, methods, staticMethods);
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            App.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
