package io.siever.lox;

import io.siever.lox.Expr.Assign;
import io.siever.lox.Expr.Binary;
import io.siever.lox.Expr.Conditional;
import io.siever.lox.Expr.Grouping;
import io.siever.lox.Expr.Literal;
import io.siever.lox.Expr.Unary;
import io.siever.lox.Expr.Variable;

import java.util.List;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  String print(Stmt stmt) {
    return stmt.accept(this);
  }

  @Override
  public String visitBlockStmt(Stmt.Block stmt) {
    StringBuilder builder = new StringBuilder();
    builder.append("(block ");

    for (Stmt statement : stmt.statements) {
      builder.append(statement.accept(this));
    }

    builder.append(")");
    return builder.toString();
  }

  @Override
  public String visitExpressionStmt(Stmt.Expression stmt) {
    return parenthesize(";", stmt.expression);
  }

  @Override
  public String visitIfStmt(Stmt.If stmt) {
    if (stmt.elseBranch == null) {
      return parenthesize2("if", stmt.condition, stmt.thenBranch);
    }

    return parenthesize2("if-else", stmt.condition, stmt.thenBranch, stmt.elseBranch);
  }

  @Override
  public String visitPrintStmt(Stmt.Print stmt) {
    return parenthesize("print", stmt.expression);
  }

  @Override
  public String visitVarStmt(Stmt.Var stmt) {
    if (stmt.initializer == null) {
      return parenthesize2("var", stmt.name);
    }

    return parenthesize2("var", stmt.name, "=", stmt.initializer);
  }

  @Override
  public String visitVariableExpr(Variable expr) {
    return expr.name.lexeme;
  }

  @Override
  public String visitAssignExpr(Assign expr) {
    return parenthesize("assign to " + expr.name.lexeme, expr.value);
  }

  @Override
  public String visitBinaryExpr(Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitConditionalExpr(Conditional expr) {
    return parenthesize("?:", expr.condition, expr.thenBranch, expr.elseBranch);
  }

  @Override
  public String visitGroupingExpr(Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Literal expr) {
    if (expr.value == null) {
      return "nil";
    }

    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  private String parenthesize2(String name, Object... parts) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    transform(builder, parts);
    builder.append(")");

    return builder.toString();
  }

  private void transform(StringBuilder builder, Object... parts) {
    for (Object part : parts) {
      builder.append(" ");
      if (part instanceof Expr) {
        builder.append(((Expr) part).accept(this));
        // > Statements and State omit
      } else if (part instanceof Stmt) {
        builder.append(((Stmt) part).accept(this));
        // < Statements and State omit
      } else if (part instanceof Token) {
        builder.append(((Token) part).lexeme);
      } else if (part instanceof List) {
        transform(builder, ((List) part).toArray());
      } else {
        builder.append(part);
      }
    }
  }

}
