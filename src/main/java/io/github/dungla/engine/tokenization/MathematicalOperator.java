package io.github.dungla.engine.tokenization;

public enum MathematicalOperator {

    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/");

    private String operator;

    MathematicalOperator(String operator) {
        this.operator = operator;
    }

    public static MathematicalOperator operator(String op) throws IllegalArgumentException {
        MathematicalOperator[] operators = MathematicalOperator.values();
        for (int i = 0; i < operators.length; i++) {
            if (operators[i].operator.equals(op)) {
                return operators[i];
            }
        }
        throw new IllegalArgumentException(MathematicalOperator.class + " wrong operator!");
    }
}
