package com.lakshya.interpreter.ast;

public abstract class Stmt {

    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
    }

    public static class Expression extends Stmt {

        public Expression(Expr expression) {
            this.expression = expression;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Print extends Stmt {

        public Print(Expr expression) {
            this.expression = expression;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
