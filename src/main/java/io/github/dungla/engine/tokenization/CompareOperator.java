package io.github.dungla.engine.tokenization;

public enum CompareOperator {
    EQUAL("=="), NOT_EQUAL("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">=");

    private String operator;

    CompareOperator(String operator) {
        this.operator = operator;
    }
    public static CompareOperator operator(String op) throws IllegalArgumentException{
        CompareOperator[] operators = CompareOperator.values();
        for (int i = 0; i < operators.length; i++) {
            if(operators[i].operator.equals(op)){
                return operators[i];
            }
        }
        throw new IllegalArgumentException(CompareOperator.class + " wrong operator!");
    }
}
