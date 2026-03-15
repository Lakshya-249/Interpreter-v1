package com.lakshya.interpreter.runtime;

import com.lakshya.interpreter.App;
import com.lakshya.interpreter.ast.Expr;
import com.lakshya.interpreter.ast.Stmt;
import com.lakshya.interpreter.lexer.Token;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

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
        return evaluate(expr);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
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
