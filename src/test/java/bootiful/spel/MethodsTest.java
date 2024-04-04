package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class MethodsTest {

    @Test
    void methods() throws Exception {
        var expressionParser = new SpelExpressionParser();
        Assertions.assertEquals(expressionParser
                .parseExpression(" 'abc'.substring(1,3) ").getValue(String.class), "bc");
    }
}
