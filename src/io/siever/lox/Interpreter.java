package io.siever.lox;

class Interpreter implements Expr.Visitor<Object> {
  void interpret(Expr expression) {
    try {
      Object value = evaluate(expression);
      System.out.println(stringify(value));
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
    // Equality
    case BANG_EQUAL:
      return !isEqual(left, right);

    case EQUAL_EQUAL:
      return isEqual(left, right);

    // Cmparison
    case GREATER:
      checkNumberOperands(expr.operator, left, right);
      return (double) left > (double) right;
    case GREATER_EQUAL:
      checkNumberOperands(expr.operator, left, right);
      return (double) left >= (double) right;
    case LESS:
      checkNumberOperands(expr.operator, left, right);
      return (double) left < (double) right;
    case LESS_EQUAL:
      checkNumberOperands(expr.operator, left, right);
      return (double) left <= (double) right;

    // Arthimetic
    case MINUS:
      checkNumberOperands(expr.operator, left, right);
      return (double) left + (double) right;

    case PLUS:
      if (left instanceof Double && right instanceof Double) {
        return (double) left + (double) right;
      }

      if (left instanceof String && right instanceof String) {
        return (String) left + (String) right;
      }
      if (left instanceof String && right instanceof Double) {
        return (String) left + stringify(right);
      }

      throw new RuntimeError(expr.operator,
          "Operands must be two numbers or two strings or an string followed by a number");

    case STAR:
      checkNumberOperands(expr.operator, left, right);
      return (double) left * (double) right;

    case SLASH:
      checkNumberOperands(expr.operator, left, right);
      if ((double) right == 0.0) {
        throw new RuntimeError(expr.operator, "Division by zero");
      }
      return (double) left / (double) right;

    // Unreachable
    default:
      return null;
    }
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
  public Object visitConditionalExpr(Expr.Conditional expr) {
    boolean cond = isTruthy(evaluate(expr.cond));

    if (cond) {
      return evaluate(expr.thenBranch);
    } else {
      return evaluate(expr.elseBranch);
    }
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
    case MINUS:
      checkNumberOperand(expr.operator, right);
      return -(double) right;

    case BANG:
      return !isTruthy(right);

    // Unreachable
    default:
      return null;
    }
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) {
      return;
    }

    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }

    throw new RuntimeError(operator, "Operands must be a number.");
  }

  private boolean isTruthy(Object object) {
    if (object == null) {
      return false;
    }

    if (object instanceof Boolean) {
      return (boolean) object;
    }

    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null) {
      return false;
    }

    return a.equals(b);
  }

  private String stringify(Object object) {
    if (object == null) {
      return "nil";
    }

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