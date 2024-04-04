package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class OperatorsTest {

    @Test
    void operators() throws Exception {
        var expressionParser = new SpelExpressionParser();
        Assertions.assertEquals(expressionParser.parseExpression(" 2 == 2 ")
                .getValue(Boolean.class), true);
        Assertions.assertEquals(expressionParser.parseExpression(" 'black'  <  'block' ")
                .getValue(Boolean.class), true);
    }

}
