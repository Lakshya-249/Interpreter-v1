package com.lakshya.interpreter.ast;

import com.lakshya.interpreter.lexer.Token;
import java.util.List;

public abstract class Stmt {

    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }

    public static class Block extends Stmt {

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        public final List<Stmt> statements;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
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

    public static class Var extends Stmt {

        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        public final Token name;
        public final Expr initializer;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
