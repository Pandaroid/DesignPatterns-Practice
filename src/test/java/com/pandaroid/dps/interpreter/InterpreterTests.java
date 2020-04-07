package com.pandaroid.dps.interpreter;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class InterpreterTests {
    @Test
    void testSpringExpression() {
        ExpressionParser ep = new SpelExpressionParser();
        Expression expression = ep.parseExpression("3 * 2 + 4 * 5");
        System.out.println("[InterpreterTests testSpringExpression] expression.getValue(): " + expression.getValue());
    }
}
