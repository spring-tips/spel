package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class LiteralExpressionsTest {

    @Test
    void literalExpressions() throws Exception {
        var expressionParser = new SpelExpressionParser();
        var expression = expressionParser.parseExpression(" 'hello, world'.concat('!').bytes ");
        var message = expression.getValue();
        if (message instanceof byte[] bytes)
            Assertions.assertEquals(new String(bytes), "hello, world!");
        else
            Assertions.fail();
    }
}
